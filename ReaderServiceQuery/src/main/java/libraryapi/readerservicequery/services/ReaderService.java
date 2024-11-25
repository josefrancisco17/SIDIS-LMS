package libraryapi.readerservicequery.services;

import jakarta.servlet.http.HttpServletRequest;
import libraryapi.readerservicequery.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
}
