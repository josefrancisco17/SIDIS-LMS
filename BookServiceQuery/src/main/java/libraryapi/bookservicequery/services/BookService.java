package libraryapi.bookservicequery.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import libraryapi.bookservicequery.model.Book;
import libraryapi.bookservicequery.model.BookCover;
import libraryapi.bookservicequery.model.Genre;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Optional<Book> getBook(String isbn);
    Page<Book> getBooks(Pageable pageable);
    Iterable<Book> getAllBooks();
    Iterable<Genre> getTopGenres();
    Iterable<Book> getTopBooks();
    Page<Book> getBooksByGenre(String genre, Pageable pageable);
    Page<Book> getBooksByTitle(String title, Pageable pageable);
    Page<Book> getBooksByAuthor(String author, Pageable pageable);
    Page<Book> getBooksByTitleAndGenreAndAuthor(String genre, String title, String author, Pageable pageable);
    BookCover getBookCover(String bookId);
    Book manageInternalBook(Book book);
    List<Book> getBooksByAuthorId(Long id);
}
