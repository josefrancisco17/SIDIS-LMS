package libraryapi.bookservicecommand.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import libraryapi.bookservicecommand.model.Book;
import libraryapi.bookservicecommand.model.BookCover;
import libraryapi.bookservicecommand.model.Genre;
import libraryapi.bookservicecommand.fileStorage.UploadFileResponse;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Book createBook(CreateBookRequest resource, MultipartFile coverPhoto);
    Book manageInternalBook(Book book);
    Book updateBook(Long id, EditBookRequest resource, long desiredVersion);
    Book partialUpdateBook(Long id, EditBookRequest resource, long desiredVersion);
    UploadFileResponse doUploadFile(String id, MultipartFile file);
}
