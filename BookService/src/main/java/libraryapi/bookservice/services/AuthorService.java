package libraryapi.bookservice.services;

import libraryapi.bookservice.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import libraryapi.bookservice.model.Author;
import libraryapi.bookservice.model.AuthorPhoto;
import libraryapi.bookservice.fileStorage.UploadFileResponse;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    int getTotalPages();
    Page<Author> getAuthors(Pageable pageable);
    Optional<Author> getAuthorsById(final Long id);
    AuthorPhoto getAuthorPhoto(final String authorId);
    List<Author> getAuthorsByName(final String name);
    List<Book> getAuthorBooks(Long authorId);
    List<Author> getTop5Authors();
    Author createAuthor(final EditAuthorRequest resource, MultipartFile authorPhoto);
    Author manageInternalAuthor(Author author);
    Author updateAuthor(final Long id, final EditAuthorRequest resource, final long desiredVersion);
    Author partialUpdateAuthor(final Long id, final EditAuthorRequest resource, final long desiredVersion);
    UploadFileResponse doUploadFile(final String id, final MultipartFile file);
    UploadFileResponse uploadAuthorPhoto(final String id, final MultipartFile file);
    void validateCreateAuthorRequest(final EditAuthorRequest request);
    List<Book> getCoAuthorsBooks(Long authorId);
}