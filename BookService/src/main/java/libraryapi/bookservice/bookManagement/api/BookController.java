package libraryapi.bookservice.bookManagement.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import libraryapi.bookservice.bookManagement.services.GenreServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import libraryapi.bookservice.bookManagement.model.Book;
import libraryapi.bookservice.bookManagement.model.BookCover;
import libraryapi.bookservice.bookManagement.repositories.BookRepository;
import libraryapi.bookservice.bookManagement.services.BookServiceImpl;
import libraryapi.bookservice.bookManagement.services.CreateBookRequest;
import libraryapi.bookservice.bookManagement.services.EditBookRequest;
import libraryapi.bookservice.exceptions.NotFoundException;
import libraryapi.bookservice.fileStorage.UploadFileResponse;
import psoftg2.libraryapi.lendingManagement.services.LendingServiceImpl;
//import psoftg2.libraryapi.userManagement.model.Role;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Tag(name = "Books", description = "Endpoints for managing Books")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/books")
public class BookController {

    private static final String IF_MATCH = "If-Match";
    private final BookServiceImpl bookService;
    private final GenreServiceImpl genreService;
    private final LendingServiceImpl lendingService;
    private final BookViewMapper bookViewMapper;
    private final GenreViewMapper genreViewMapper;
    private final LentBookViewMapper lentBookViewMapper;
    private final BookRepository bookRepository;

    @Operation(summary = "Gets a specific Book")
    @GetMapping("/{bookIsbn}")
    //@RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    public ResponseEntity<BookView> getBook(@PathVariable("bookIsbn") String isbn) {
        final var book = bookService.getBook(isbn).orElseThrow(() -> new NotFoundException(Book.class, isbn));

        return ResponseEntity.ok().eTag(Long.toString(book.getVersion())).body(bookViewMapper.toBookView(book));
    }

    @Operation(summary = "Gets a specific Genre")
    @GetMapping("/genres/{genreId}")
    //@RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    public ResponseEntity<GenreView> getGenreById(@PathVariable("genreId") Long genreId) {
        final var genre = genreService.getGenreById(genreId).orElseThrow(() -> new NotFoundException(Book.class, genreId));

        return ResponseEntity.ok().eTag(Long.toString(genre.getVersion())).body(genreViewMapper.toGenreView(genre, bookService.getAllBooks()));
    }

    @Operation(summary = "Gets all Books")
    @GetMapping
    //@RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
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

    @Operation(summary = "Gets top 5 Genres by book number")
    @GetMapping("/top-genres")
    //@RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    @ApiResponse(description = "Success", responseCode = "200", content = { @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = BookView.class))) })
    public Iterable<GenreView> getTopGenres() {

        return genreViewMapper.toGenreView(bookService.getTopGenres(), bookService.getAllBooks());
    }

    @Operation(summary = "Gets top 5 Books lent")
    @GetMapping("/top-books")
    //@RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    @ApiResponse(description = "Success", responseCode = "200", content = { @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = BookView.class))) })
    public Iterable<LentBookView> getTopBooks() {
        return lentBookViewMapper.toLentBookView(bookService.getTopBooks(), lendingService.getAllLendings());
    }

    @Operation(summary = "Downloads a cover of a book by id")
    @GetMapping("/{bookId}/cover")
    //@RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    public ResponseEntity<Resource> getBookCover(@PathVariable("bookId") final String bookId,
                                                 final HttpServletRequest request) {

        BookCover bookCover = bookService.getBookCover(bookId);

        final Resource resource = new ByteArrayResource(bookCover.getImage());

        String contentType = bookCover.getContentType();

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @Operation(summary = "Uploads a cover of a Book")
    @PostMapping("/{bookId}/cover")
    @ResponseStatus(HttpStatus.CREATED)
    //@RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    public ResponseEntity<UploadFileResponse> uploadFile(@PathVariable("bookId") final String bookId,
                                                         @RequestParam("file") final MultipartFile file) throws URISyntaxException {

        final UploadFileResponse up = bookService.doUploadFile(bookId, file);

        return ResponseEntity.created(new URI(up.getFileDownloadUri())).body(up);

    }

    private Long getVersionFromIfMatchHeader(final String ifMatchHeader) {
        if (ifMatchHeader.startsWith("\"")) {
            return Long.parseLong(ifMatchHeader.substring(1, ifMatchHeader.length() - 1));
        }
        return Long.parseLong(ifMatchHeader);
    }

    @Operation(summary = "Creates a new Book")
    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    //@RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    public ResponseEntity<BookView> createBook(@Valid @RequestPart("book") final CreateBookRequest resource,
                                               @RequestPart(value = "cover", required = false) MultipartFile coverPhoto) {

        final var book = bookService.createBook(resource, coverPhoto);

        final var newbarUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(book.getId().toString())
                .build().toUri();

        return ResponseEntity.created(newbarUri).eTag(Long.toString(book.getVersion()))
                .body(bookViewMapper.toCreateBookView(book));
    }

    @Operation(summary = "Fully replaces an existing book")
    @PutMapping(path = "{bookId}")
    //@RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    public ResponseEntity<BookView> updateBook(final WebRequest request,
            @PathVariable("bookId") Long id,
            @Valid @RequestBody final EditBookRequest resource) {
        final String ifMatchValue = request.getHeader(IF_MATCH);
        if (ifMatchValue == null || ifMatchValue.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Book book = bookService.updateBook(id, resource, getVersionFromIfMatchHeader(ifMatchValue));
        return ResponseEntity.ok().eTag(Long.toString(book.getVersion())).body(bookViewMapper.toBookView(book));
    }

    @Operation(summary = "Partially updates an existing book")
    @PatchMapping(path = "{bookId}")
    //@RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    public ResponseEntity<BookView> partialUpdateBook(final WebRequest request,
                                                    @PathVariable("bookId") Long id,
                                                    @Valid @RequestBody final EditBookRequest resource) {
        final String ifMatchValue = request.getHeader(IF_MATCH);
        if (ifMatchValue == null || ifMatchValue.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Book book = bookService.partialUpdateBook(id, resource, getVersionFromIfMatchHeader(ifMatchValue));
        return ResponseEntity.ok().eTag(Long.toString(book.getVersion())).body(bookViewMapper.toBookView(book));
    }
}


