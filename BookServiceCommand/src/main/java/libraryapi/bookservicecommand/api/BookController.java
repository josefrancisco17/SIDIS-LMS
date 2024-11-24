package libraryapi.bookservicecommand.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import libraryapi.bookservicecommand.model.*;
import libraryapi.bookservicecommand.repositories.BookRepositoryHTTP;
import libraryapi.bookservicecommand.repositories.LendingRepositoryHTTP;
import libraryapi.bookservicecommand.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import libraryapi.bookservicecommand.exceptions.NotFoundException;

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

        @Operation(summary = "Creates a new Book")
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
        public ResponseEntity<BookView> createBook(@Valid @RequestPart("book") final CreateBookRequest resource,
                                                   @RequestPart(value = "cover", required = false) MultipartFile coverPhoto) {

            final var book = bookService.createBook(resource, coverPhoto);

            final var newbarUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(book.getId().toString())
                    .build().toUri();

            return ResponseEntity.created(newbarUri).eTag(Long.toString(book.getVersion()))
                    .body(bookViewMapper.toCreateBookView(book));
        }

        @Operation(summary = "Handles Creation, Update and Patch of Books in another instances")
        @PutMapping("/internal")
        @ResponseStatus(HttpStatus.CREATED)
        public ResponseEntity<BookView> manageInternalBook(@Valid @RequestBody Book book) {
            Book newBook = bookService.manageInternalBook(book);

            final var newbarUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(newBook.getId().toString())
                    .build().toUri();

            return ResponseEntity.created(newbarUri).eTag(Long.toString(newBook.getVersion()))
                    .body(bookViewMapper.toBookView(newBook));
        }

        @Operation(summary = "Fully replaces an existing book")
        @PutMapping(path = "{bookId}")
        @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
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
        @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
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

    private Long getVersionFromIfMatchHeader(final String ifMatchHeader) {
        if (ifMatchHeader.startsWith("\"")) {
            return Long.parseLong(ifMatchHeader.substring(1, ifMatchHeader.length() - 1));
        }
        return Long.parseLong(ifMatchHeader);
    }
}
