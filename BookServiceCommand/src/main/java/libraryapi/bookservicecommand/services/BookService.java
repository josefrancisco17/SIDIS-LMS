package libraryapi.bookservicecommand.services;

import org.springframework.web.multipart.MultipartFile;
import libraryapi.bookservicecommand.model.Book;
import libraryapi.bookservicecommand.fileStorage.UploadFileResponse;


public interface BookService {
    Book createBook(CreateBookRequest resource, MultipartFile coverPhoto);
    Book manageInternalBook(Book book);
    Book updateBook(Long id, EditBookRequest resource, long desiredVersion);
    Book partialUpdateBook(Long id, EditBookRequest resource, long desiredVersion);
    UploadFileResponse doUploadFile(String id, MultipartFile file);
}
