package libraryapi.bookservicecommand.services;

import libraryapi.bookservicecommand.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import libraryapi.bookservicecommand.model.Author;
import libraryapi.bookservicecommand.model.AuthorPhoto;
import libraryapi.bookservicecommand.fileStorage.UploadFileResponse;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    Author createAuthor(final EditAuthorRequest resource, MultipartFile authorPhoto);
    Author manageInternalAuthor(Author author);
    Author updateAuthor(final Long id, final EditAuthorRequest resource, final long desiredVersion);
    Author partialUpdateAuthor(final Long id, final EditAuthorRequest resource, final long desiredVersion);
    UploadFileResponse doUploadFile(final String id, final MultipartFile file);
    void validateCreateAuthorRequest(final EditAuthorRequest request);
}