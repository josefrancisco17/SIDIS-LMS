package libraryapi.bookservice.services;

import libraryapi.bookservice.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import libraryapi.bookservice.model.Book;
import libraryapi.bookservice.model.BookCover;
import libraryapi.bookservice.model.Genre;
import libraryapi.bookservice.fileStorage.UploadFileResponse;

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
    Book createBook(CreateBookRequest resource, MultipartFile coverPhoto);
    Book manageInternalBook(Book book);
    Book updateBook(Long id, EditBookRequest resource, long desiredVersion);
    Book partialUpdateBook(Long id, EditBookRequest resource, long desiredVersion);
    UploadFileResponse uploadBookCover(final String id, final MultipartFile file);
    UploadFileResponse doUploadFile(String id, MultipartFile file);
    List<Book> getBooksByAuthorId(Long id);
}
