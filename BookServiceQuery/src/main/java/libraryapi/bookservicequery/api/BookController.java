package libraryapi.bookservicequery.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import libraryapi.bookservicequery.model.*;
import libraryapi.bookservicequery.model.Book;
import libraryapi.bookservicequery.model.BookCover;
import libraryapi.bookservicequery.model.Genre;
import libraryapi.bookservicequery.model.Role;
import libraryapi.bookservicequery.repositories.BookRepositoryHTTP;
import libraryapi.bookservicequery.repositories.LendingRepositoryHTTP;
import libraryapi.bookservicequery.services.*;
import libraryapi.bookservicequery.services.BookServiceImpl;
import libraryapi.bookservicequery.services.CreateBookRequest;
import libraryapi.bookservicequery.services.EditBookRequest;
import libraryapi.bookservicequery.services.GenreServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import libraryapi.bookservicequery.exceptions.NotFoundException;

import java.util.List;

@Tag(name = "Books", description = "Endpoints for managing Books")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/books")
public class BookController {

    private static final String IF_MATCH = "If-Match";
    private final BookServiceImpl bookService;
    private final BookViewMapper bookViewMapper;
    private final GenreViewMapper genreViewMapper;
    private final LentBookViewMapper lentBookViewMapper;
    private final BookRepositoryHTTP bookRepositoryHTTP;
    private final GenreServiceImpl genreService;
    private final LendingRepositoryHTTP lendingRepositoryHTTP;

    @Operation(summary = "Gets a specific Book")
    @GetMapping("/{bookIsbn}")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    public ResponseEntity<BookView> getBook(@PathVariable("bookIsbn") String isbn) {
        final var book = bookService.getBook(isbn).orElseThrow(() -> new NotFoundException(Book.class, isbn));

        return ResponseEntity.ok().eTag(Long.toString(book.getVersion())).body(bookViewMapper.toBookView(book));
    }

    @Operation(summary = "Gets all Books")
    @GetMapping
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    @ApiResponse(description = "Success", responseCode = "200", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookView.class)))
    })
    public List<BookView> getBooks(
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "100", required = false) int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> booksPage;

        if (genre != null && title != null && author != null) {
            booksPage = bookService.getBooksByTitleAndGenreAndAuthor(genre, title, author, pageable);
        } else if (genre != null && title == null && author == null) {
            booksPage = bookService.getBooksByGenre(genre, pageable);
        } else if (genre == null && title != null && author == null) {
            booksPage = bookService.getBooksByTitle(title, pageable);
        } else if (genre == null && title == null && author != null) {
            booksPage = bookService.getBooksByAuthor(author, pageable);
        } else {
            booksPage = bookService.getBooks(pageable);
        }

        return booksPage.map(bookViewMapper::toBookView).getContent();
    }

    @Operation(summary = "Gets all Books for other services")
    @GetMapping("/internal")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    @ApiResponse(description = "Success", responseCode = "200", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Book.class)))
    })
    public Iterable<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @Operation(summary = "Gets all Genres for other services")
    @GetMapping("/internal/genres")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    @ApiResponse(description = "Success", responseCode = "200", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Genre.class)))
    })
    public Iterable<Genre> getAllGenres() {
        return genreService.getAllGenres();
    }


    @Operation(summary = "Gets top 5 Genres by book number")
    @GetMapping("/top-genres")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    @ApiResponse(description = "Success", responseCode = "200", content = { @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = BookView.class))) })
    public Iterable<GenreView> getTopGenres() {

        return genreViewMapper.toGenreView(bookService.getTopGenres(), bookService.getAllBooks());
    }

    @Operation(summary = "Gets top 5 Books lent")
    @GetMapping("/top-books")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    @ApiResponse(description = "Success", responseCode = "200", content = { @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = BookView.class))) })
    public Iterable<LentBookView> getTopBooks() {
        return lentBookViewMapper.toLentBookView(bookService.getTopBooks());
    }

    @Operation(summary = "Downloads a cover of a book by id")
    @GetMapping("/{bookId}/cover")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    public ResponseEntity<Resource> getBookCover(@PathVariable("bookId") final String bookId,
                                                 final HttpServletRequest request) {

        BookCover bookCover = bookService.getBookCover(bookId);

        final Resource resource = new ByteArrayResource(bookCover.getImage());

        String contentType = bookCover.getContentType();

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
