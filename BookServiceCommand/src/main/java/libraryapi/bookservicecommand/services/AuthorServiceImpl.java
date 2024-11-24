package libraryapi.bookservicecommand.services;

import jakarta.transaction.Transactional;
import libraryapi.bookservicecommand.model.*;
import libraryapi.bookservicecommand.rabbitMQ.producer.Sender;
import libraryapi.bookservicecommand.repositories.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import libraryapi.bookservicecommand.exceptions.NotFoundException;
import libraryapi.bookservicecommand.fileStorage.FileStorageService;
import libraryapi.bookservicecommand.fileStorage.UploadFileResponse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static libraryapi.bookservicecommand.util.AuthorUtil.isValidAuthorPhoto;

@Service
public class AuthorServiceImpl implements AuthorService{
    private final AuthorRepository authorRepository;
    private final EditAuthorMapper editAuthorMapper;
    private final AuthorPhotoRepository authorPhotoRepository;
    private final FileStorageService fileStorageService;
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final BookRepositoryHTTP bookRepositoryHTTP;
    private final AuthorRepositoryHTTP authorRepositoryHTTP;
    private final LendingRepositoryHTTP lendingRepositoryHTTP;

    @Autowired
    private Sender sender;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository, EditAuthorMapper editAuthorMapper, AuthorPhotoRepository authorPhotoRepository, FileStorageService fileStorageService, BookService bookService, BookRepository bookRepository, BookRepositoryHTTP bookRepositoryHTTP, AuthorRepositoryHTTP authorRepositoryHTTP, LendingRepositoryHTTP lendingRepositoryHTTP) {
        this.authorRepository = authorRepository;
        this.editAuthorMapper = editAuthorMapper;
        this.authorPhotoRepository = authorPhotoRepository;
        this.fileStorageService = fileStorageService;
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.bookRepositoryHTTP = bookRepositoryHTTP;
        this.authorRepositoryHTTP = authorRepositoryHTTP;
        this.lendingRepositoryHTTP = lendingRepositoryHTTP;
    }

    public Author createAuthor(final EditAuthorRequest resource, MultipartFile authorPhoto) {
        validateCreateAuthorRequest(resource);

        Author author = editAuthorMapper.create(resource);

        authorRepository.save(author);

        if (authorPhoto != null) {
            doUploadFile(author.getId().toString(), authorPhoto);
        }

        //Gets new author after with or without the photo for being sent to another instances
        Author newAuthor = authorRepository.getById(author.getId());
        //authorRepositoryHTTP.manageInternalAuthor(newAuthor);
        try {
            sender.sendSyncAuthor(newAuthor);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return authorRepository.save(author);
    }

    @Transactional
    public Author manageInternalAuthor(Author newAuthor) {
        return authorRepository.save(newAuthor);
    }


    @Transactional
    public Author updateAuthor(final Long id, final EditAuthorRequest resource, final long desiredVersion) {
        final var author = authorRepository.findById(id).orElseThrow(() -> new NotFoundException("[ERROR] Cannot update an object that does not yet exist"));
        validateCreateAuthorRequest(resource);
        author.updateData(desiredVersion, resource.getName(), resource.getShortBio());
        //Gets new author after with or without the photo for being sent to another instances
        Author newAuthor = authorRepository.getById(author.getId());
        //authorRepositoryHTTP.manageInternalAuthor(newAuthor);
        try {
            sender.sendSyncAuthor(newAuthor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authorRepository.save(author);
    }

    public Author partialUpdateAuthor(final Long id, final EditAuthorRequest resource, final long desiredVersion) {
        final var author = authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[ERROR] Cannot update an object that does not yet exist"));

        author.applyPatch(desiredVersion, resource.getName(), resource.getShortBio());
        //Gets new author after with or without the photo for being sent to another instances
        Author newAuthor = authorRepository.getById(author.getId());
        //authorRepositoryHTTP.manageInternalAuthor(newAuthor);
        try {
            sender.sendSyncAuthor(newAuthor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authorRepository.save(author);
    }

    public UploadFileResponse doUploadFile(final String id, final MultipartFile file) {
        if (isValidAuthorPhoto(file)) {
            AuthorPhoto authorPhoto = new AuthorPhoto();
            try {
                authorPhoto.setImage(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            authorPhoto.setContentType(file.getContentType());
            authorPhotoRepository.save(authorPhoto);
            Author author = authorRepository.getById(Long.parseLong(id));
            author.setAuthorPhoto(authorPhoto);
            authorRepository.save(author);
        }

        final String fileName = fileStorageService.storeFile(id, file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(fileName)
                .toUriString();

        fileDownloadUri = fileDownloadUri.replace("/photos/", "/photo/");

        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    public void validateCreateAuthorRequest(final EditAuthorRequest request) {
        if (StringUtils.isBlank(request.getName()) || StringUtils.isBlank(request.getShortBio())) {
            throw new IllegalArgumentException("[ERROR] Name and shortBio are mandatory.");
        }
    }

    public List<Book> getCoAuthorsBooks(Long authorId) {
        List<Book> allBooks = bookRepository.findBooksByAuthorId(authorId);
        Set<Book> coAuthorBooks = new HashSet<>();
        for (Book book : allBooks) {
            if (book.getAuthors().size() > 1) {
                coAuthorBooks.add(book);
            }
        }

        return new ArrayList<>(coAuthorBooks);
    }
}