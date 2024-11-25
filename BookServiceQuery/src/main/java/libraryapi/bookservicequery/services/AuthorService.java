package libraryapi.bookservicequery.services;

import libraryapi.bookservicequery.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import libraryapi.bookservicequery.model.Author;
import libraryapi.bookservicequery.model.AuthorPhoto;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    Page<Author> getAuthors(Pageable pageable);
    Optional<Author> getAuthorsById(final Long id);
    AuthorPhoto getAuthorPhoto(final String authorId);
    List<Author> getAuthorsByName(final String name);
    List<Book> getAuthorBooks(Long authorId);
    List<Author> getTop5Authors();
    List<Book> getCoAuthorsBooks(Long authorId);
}