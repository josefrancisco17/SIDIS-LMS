package libraryapi.readerservicequery.services;

import jakarta.servlet.http.HttpServletRequest;
import libraryapi.readerservicequery.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import libraryapi.readerservicequery.fileStorage.UploadFileResponse;

import java.util.Optional;

public interface ReaderService {
    Page<Reader> getReaders(Pageable pageable);
    Iterable<Reader> getAllReaders();
    Iterable<Reader> getTopReaders();
    Optional<Reader> getReader(Long id, HttpServletRequest request);
    Page<Book> getSuggestedBooks(Long readerId, Pageable pageable);
    Page<Reader> getReadersByPhoneNumberAndEmail(final String phoneNumber, final String email, Pageable pageable);
    Page<Reader> getReadersByPhoneNumber(final String phoneNumber, Pageable pageable);
    Page<Reader> getReadersByEmail(final String email, Pageable pageable);
    ReaderPhoto getReaderPhoto(final String readerId);
    Reader createReader(final EditReaderRequest resource, MultipartFile photo);
    Reader manageInternalReader(Reader reader);
    Reader updateReader(final Long id, final EditReaderRequest resource, final long desiredVersion);
    Reader partialUpdateReader(final Long id, final EditReaderRequest resource, final long desiredVersion);
    UploadFileResponse doUploadFile(final String id, final MultipartFile file);
}
