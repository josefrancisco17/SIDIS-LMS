package libraryapi.bookservicecommand.services;

import org.springframework.web.multipart.MultipartFile;
import libraryapi.bookservicecommand.model.Author;
import libraryapi.bookservicecommand.fileStorage.UploadFileResponse;

public interface AuthorService {
    Author createAuthor(final EditAuthorRequest resource, MultipartFile authorPhoto);
    Author manageInternalAuthor(Author author);
    Author updateAuthor(final Long id, final EditAuthorRequest resource, final long desiredVersion);
    Author partialUpdateAuthor(final Long id, final EditAuthorRequest resource, final long desiredVersion);
    UploadFileResponse doUploadFile(final String id, final MultipartFile file);
    void validateCreateAuthorRequest(final EditAuthorRequest request);
}