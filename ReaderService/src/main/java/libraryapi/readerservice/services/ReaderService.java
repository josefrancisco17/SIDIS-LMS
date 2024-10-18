package libraryapi.readerservice.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import libraryapi.readerservice.model.Book;
import libraryapi.readerservice.model.Genre;
import libraryapi.readerservice.fileStorage.UploadFileResponse;
import libraryapi.readerservice.model.Reader;
import libraryapi.readerservice.model.ReaderPhoto;

import java.time.LocalDate;
import java.util.Optional;

public interface ReaderService {
    Page<Reader> getReaders(Pageable pageable);
    Iterable<Reader> getAllReaders();
    Iterable<Reader> getTopReaders();
    Optional<Reader> getReaderByIdWithQuote(Long id);
    Page<Book> getSuggestedBooks(Long readerId, Pageable pageable);
    Page<Reader> getReadersByPhoneNumberAndEmail(final String phoneNumber, final String email, Pageable pageable);
    Page<Reader> getReadersByPhoneNumber(final String phoneNumber, Pageable pageable);
    Page<Reader> getReadersByEmail(final String email, Pageable pageable);
    ReaderPhoto getReaderPhoto(final String readerId);
    Reader createReader(final EditReaderRequest resource, MultipartFile photo);
    Reader updateReader(final Long id, final EditReaderRequest resource, final long desiredVersion);
    Reader partialUpdateReader(final Long id, final EditReaderRequest resource, final long desiredVersion);
    UploadFileResponse doUploadFile(final String id, final MultipartFile file);
}
