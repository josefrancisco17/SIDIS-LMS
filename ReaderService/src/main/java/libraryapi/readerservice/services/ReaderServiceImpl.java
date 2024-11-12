package libraryapi.readerservice.services;

import jakarta.transaction.Transactional;
import libraryapi.readerservice.model.*;
import libraryapi.readerservice.repositories.*;
import libraryapi.readerservice.services.producer.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import libraryapi.readerservice.util.BookUtil;
import libraryapi.readerservice.exceptions.NotFoundException;
import libraryapi.readerservice.fileStorage.FileStorageService;
import libraryapi.readerservice.fileStorage.UploadFileResponse;
import libraryapi.readerservice.util.ReaderUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

import static libraryapi.readerservice.util.ReaderUtil.isValidReaderPhoto;

@Service
public class ReaderServiceImpl implements ReaderService {

    private final ReaderRepository readerRepository;
    private final EditReaderMapper editReaderMapper;
    private final FileStorageService fileStorageService;
    private final ReaderPhotoRepository readerPhotoRepository;
    private final ReaderRepositoryHTTP readerRepositoryHTTP;
    private final LendingRepositoryHTTP lendingRepositoryHTTP;
    private final BookRepositoryHTTP bookRepositoryHTTP;

    @Autowired
    private Sender sender;

    @Autowired
    public ReaderServiceImpl(ReaderRepository readerRepository, EditReaderMapper editReaderMapper, ReaderPhotoRepository readerPhotoRepository, FileStorageService fileStorageService, ReaderRepositoryHTTP readerRepositoryHTTP, LendingRepositoryHTTP lendingRepositoryHTTP, BookRepositoryHTTP bookRepositoryHTTP) {
        this.readerRepository = readerRepository;
        this.editReaderMapper = editReaderMapper;
        this.fileStorageService = fileStorageService;
        this.readerPhotoRepository = readerPhotoRepository;
        this.readerRepositoryHTTP = readerRepositoryHTTP;
        this.lendingRepositoryHTTP = lendingRepositoryHTTP;
        this.bookRepositoryHTTP = bookRepositoryHTTP;
    }

    public Page<Reader> getReadersByName(final String name, Pageable pageable) {
        List<Reader> filteredReaders =  readerRepository.findAll()
                .stream()
                .filter(reader -> reader.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
        return toPage(filteredReaders, pageable);
    }

    public Page<Reader> getReaders(Pageable pageable) {
        List<Reader> readers = readerRepository.findAll(pageable).getContent();
        readers.forEach(this::updateAge);
        return new PageImpl<>(readers, pageable, readerRepository.count());    }

    public Iterable<Reader> getAllReaders() {
        List<Reader> readers = readerRepository.findAll();
        readers.forEach(this::updateAge);
        return readers;
    }

    public Iterable<Reader> getTopReaders() {
        List<Lending> lendings = lendingRepositoryHTTP.getAllLendings();
        Map<Long, Long> lendingCountMap = lendings.stream()
                .collect(Collectors.groupingBy(
                        Lending::getReaderId,
                        Collectors.counting()
                ));

        List<Long> topReaderIds = lendingCountMap.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .limit(6)
                .map(Map.Entry::getKey)
                .toList();

        return topReaderIds.stream()
                .map(readerId -> readerRepository.findById(readerId).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Optional<Reader> getReaderByIdWithQuote(Long id) {
        Optional<Reader> readerOpt = readerRepository.findReaderById(id);
        readerOpt.ifPresent(this::updateAge);
       if (readerOpt.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] Cannot find Reader");
        }
        return readerOpt;
    }

    public Page<Book> getSuggestedBooks(Long readerId, Pageable pageable) {
        Reader reader = readerRepository.findById(readerId).orElseThrow(() -> new NotFoundException("Reader not found with id: " + readerId));
        List<String> interests = reader.getInterests();

        if (interests == null || interests.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] Reader does not have any interests specified.");
        }

        List<Book> suggestedBooks = bookRepositoryHTTP.getAllBooks().stream().filter(book -> interests.contains(book.getGenre().getName())).toList();
        return BookUtil.toPage(suggestedBooks, pageable);
    }

    public Page<Reader> getReadersByPhoneNumberAndEmail(final String phoneNumber, final String email, Pageable pageable) {
        List<Reader> filteredReaders = readerRepository.findAll().stream()
                .filter(reader -> reader.getPhoneNumber().toString().toLowerCase().contains(phoneNumber.toLowerCase()) &&
                        reader.getEmail().toLowerCase().contains(email.toLowerCase()))
                .collect(Collectors.toList());
        filteredReaders.forEach(this::updateAge);
        return toPage(filteredReaders, pageable);
    }

    public Page<Reader> getReadersByPhoneNumber(final String phoneNumber, Pageable pageable) {
        List<Reader> filteredReaders = readerRepository.findAll()
                .stream()
                .filter(reader -> reader.getPhoneNumber().toString().contains(phoneNumber))
                .collect(Collectors.toList());
        filteredReaders.forEach(this::updateAge);
        return toPage(filteredReaders, pageable);
    }

    public Page<Reader> getReadersByEmail(final String email, Pageable pageable) {
        List<Reader> filteredReaders =  readerRepository.findAll()
                .stream()
                .filter(reader -> reader.getEmail().contains(email))
                .collect(Collectors.toList());
        filteredReaders.forEach(this::updateAge);
        return toPage(filteredReaders, pageable);
    }

    public ReaderPhoto getReaderPhoto(final String readerId) {
        final var existingReader = readerRepository.findById(Long.parseLong(readerId)).orElseThrow(() -> new NotFoundException("[ERROR] Reader not found"));

        if (existingReader.getReaderPhoto() == null) {
            throw new IllegalArgumentException("[ERROR] Reader Photo not found with ID: " + existingReader.getId());
        }

        return existingReader.getReaderPhoto();
    }

    public Reader createReader(final EditReaderRequest resource, MultipartFile photo) {
        validateCreateReaderRequest(resource);
        Reader reader = editReaderMapper.create(resource);
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(reader.getDateOfBirth(), currentDate);
        Integer age = period.getYears();
        reader.setAge(age);
        String readerCode = java.time.Year.now().getValue() + "/" + (readerRepository.findMaxReaderId() + 1);
        reader.setReaderCode(readerCode);
        reader.setInterests(resource.getInterests());

        readerRepository.save(reader);

        if (photo != null) {
            doUploadFile(reader.getId().toString(), photo);
        }

        Reader newReader = readerRepository.getById(reader.getId());
        //readerRepositoryHTTP.manageInternalReader(newReader);
        try {
            sender.sendReaderUpdate(newReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readerRepository.save(reader);
    }

    public Reader manageInternalReader(Reader reader) {
        return readerRepository.save(reader);
    }

    @Transactional
    public Reader updateReader(final Long id, final EditReaderRequest resource, final long desiredVersion) {
        final var reader = readerRepository.findById(id).orElseThrow(() -> new NotFoundException("[ERROR] Cannot update an object that does not yet exist"));
        reader.updateData(desiredVersion, resource.getName(), resource.getEmail(), resource.getDateOfBirth(), resource.getPhoneNumber(), resource.getGDBRConsent(), resource.getInterests());
        readerRepositoryHTTP.manageInternalReader(reader);
        return readerRepository.save(reader);
    }

    public Reader partialUpdateReader(final Long id, final EditReaderRequest resource, final long desiredVersion) {
        final var reader = readerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[ERROR] Cannot update an object that does not yet exist"));
        reader.applyPatch(desiredVersion, resource.getName(), resource.getEmail(), resource.getDateOfBirth(), resource.getPhoneNumber(), resource.getGDBRConsent(), resource.getInterests());
        readerRepositoryHTTP.manageInternalReader(reader);
        return readerRepository.save(reader);
    }

    private void validateCreateReaderRequest(final EditReaderRequest request) {

        if (!request.getGDBRConsent()) {
            throw new IllegalArgumentException("[ERROR] GDBR consent must be true to register a new reader");
        }

        Optional<Reader> existingReader = readerRepository.findByEmail(request.getEmail());
        if (existingReader.isPresent()) {
            throw new IllegalArgumentException("[ERROR] Reader with email " + request.getEmail() + " already exists");
        }

        if (!ReaderUtil.isValidDateOfBirth(request.getDateOfBirth())) {
            throw new IllegalArgumentException("[ERROR] Invalid date of birth. Reader must be at least 12 years old.");
        }

        if (request.getPhoneNumber() == null || request.getPhoneNumber().toString().length() != 9) {
            throw new IllegalArgumentException("[ERROR] Phone number must be exactly 9 digits long.");
        }

        if (!ReaderUtil.isValidName(request.getName())) {
            throw new IllegalArgumentException("[ERROR] Invalid reader name.");
        }
        validateGenres(request.getInterests());

    }

    private void validateGenres(List<String> interests) {
        if (interests != null && !interests.isEmpty()) {
            List<String> invalidGenres = interests.stream()
                    .filter(genre -> !ReaderUtil.VALID_GENRES.contains(genre))
                    .collect(Collectors.toList());
            if (!invalidGenres.isEmpty()) {
                throw new IllegalArgumentException("[ERROR] Invalid genres: " + String.join(", ", invalidGenres));
            }
        }
    }

    public UploadFileResponse doUploadFile(final String id, final MultipartFile file) {
        if (isValidReaderPhoto(file)) {
            ReaderPhoto photo = new ReaderPhoto();
            try {
                photo.setImage(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            photo.setContentType(file.getContentType());
            readerPhotoRepository.save(photo);
            Reader reader = readerRepository.getById(Long.parseLong(id));
            reader.setReaderPhoto(photo);
            readerRepository.save(reader);

        }

        final String fileName = fileStorageService.storeFile(id, file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(fileName)
                .toUriString();

        fileDownloadUri = fileDownloadUri.replace("/photos/", "/photo/");

        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    private Page<Reader> toPage(List<Reader> readers, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), readers.size());
        List<Reader> sublist = readers.subList(start, end);
        sublist.forEach(this::updateAge);
        return new PageImpl<>(sublist, pageable, readers.size());
    }

    private void updateAge(Reader reader) {
        if (reader.getDateOfBirth() != null) {
            reader.setAge(Period.between(reader.getDateOfBirth(), LocalDate.now()).getYears());
        }
    }
}
