package libraryapi.bookservicequery.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libraryapi.bookservicequery.model.*;
import libraryapi.bookservicequery.model.Author;
import libraryapi.bookservicequery.model.AuthorPhoto;
import libraryapi.bookservicequery.model.Book;
import libraryapi.bookservicequery.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import libraryapi.bookservicequery.services.AuthorServiceImpl;
import libraryapi.bookservicequery.services.EditAuthorRequest;
import libraryapi.bookservicequery.services.BookServiceImpl;
import libraryapi.bookservicequery.exceptions.NotFoundException;
import java.util.List;

@Tag(name = "Authors", description = "Endpoints for managing Authors")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/authors")
public class AuthorController {

    private static final String IF_MATCH = "If-Match";
    private final AuthorServiceImpl authorService;
    private final BookServiceImpl bookService;
    private final AuthorViewMapper authorViewMapper;
    private final BookViewMapper bookViewMapper;
    private final AuthorLentsViewMapper authorLentsViewMapper;

    @Operation(summary = "Gets all Authors")
    @GetMapping
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    @ApiResponse(description = "Success", responseCode = "200", content = { @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = AuthorView.class))) })
    public List<AuthorView> getAuthors(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "100", required = false) int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Author> authorPage = authorService.getAuthors(pageable);
        return authorPage.map(authorViewMapper::toAuthorView).getContent();
    }

    @Operation(summary = "Gets a specific Author by id")
    @GetMapping("/{authorId}")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    public ResponseEntity<AuthorView> getAuthor(@PathVariable("authorId") Long id) {
        final var author = authorService.getAuthorsById(id).orElseThrow(() -> new NotFoundException(Author.class, id));
        return ResponseEntity.ok().eTag(Long.toString(author.getVersion())).body(authorViewMapper.toAuthorView(author));
    }

    @Operation(summary = "Gets a specific Author by name")
    @GetMapping("/name")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    public Iterable<AuthorView> getAuthors(@RequestParam String name) {
        return authorViewMapper.toAuthorView(authorService.getAuthorsByName(name));
    }

    @Operation(summary = "Gets the co-authors of an author and their respective books")
    @GetMapping("/{authorId}/co-authors")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    public List<BookView> getAuthorCoAuthors(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "100", required = false) int size,
            @PathVariable("authorId") Long authorId) {

        Pageable pageable = PageRequest.of(page, size);
        List<Book> booksList = authorService.getCoAuthorsBooks(authorId);

        return getBookViews(pageable, booksList);
    }

    @Operation(summary = "Gets the top-5 authors")
    @GetMapping("/top-authors")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    @ApiResponse(description = "Success", responseCode = "200", content = { @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = AuthorView.class))) })
    public Iterable<AuthorLentsView> getTop5Authors() {

        final var authorList = authorService.getTop5Authors();

        return authorLentsViewMapper.toAuthorLentsView(authorList);
    }

    @Operation(summary = "Gets the books from a specific Author by its id")
    @GetMapping("/{authorId}/books")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    public List<BookView> getAuthorBooks(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "100", required = false) int size,
            @PathVariable("authorId") Long authorId) {


        Pageable pageable = PageRequest.of(page, size);
        List<Book> booksList = authorService.getAuthorBooks(authorId);

        return getBookViews(pageable, booksList);
    }

    @Operation(summary = "Downloads a photo of an author by id")
    @GetMapping("/{authorId}/photo")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    public ResponseEntity<Resource> getBookCover(@PathVariable("authorId") final String authorId) {

        AuthorPhoto authorPhoto = authorService.getAuthorPhoto(authorId);

        final Resource resource = new ByteArrayResource(authorPhoto.getImage());

        String contentType = authorPhoto.getContentType();

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @Operation(summary = "Creates a new Author")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    public ResponseEntity<AuthorView> createAuthor(@Valid @RequestPart("author") final EditAuthorRequest resource,
                                                   @RequestPart(value = "authorPhoto", required = false) MultipartFile authorPhoto) {

        final var author = authorService.createAuthor(resource, authorPhoto);

        final var newbarUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(author.getId().toString())
                .build().toUri();

        return ResponseEntity.created(newbarUri).eTag(Long.toString(author.getVersion()))
                .body(authorViewMapper.toAuthorView(author));
    }

    @Operation(summary = "Handles Creation, Update and Patch of Author in another instances")
    @PutMapping("/internal")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthorView> manageInternalReader(@Valid @RequestBody Author author) {
        Author newAuthor = authorService.manageInternalAuthor(author);

        final var newbarUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(newAuthor.getId().toString())
                .build().toUri();

        return ResponseEntity.created(newbarUri).eTag(Long.toString(newAuthor.getVersion()))
                .body(authorViewMapper.toAuthorView(newAuthor));
    }

    @Operation(summary = "Fully replaces an existing author")
    @PutMapping(path = "{authorId}")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    public ResponseEntity<AuthorView> updateAuthor(final WebRequest request,
                                                   @PathVariable("authorId") Long id,
                                                   @Valid @RequestBody final EditAuthorRequest resource) {
        final String ifMatchValue = request.getHeader(IF_MATCH);
        if (ifMatchValue == null || ifMatchValue.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Author author = authorService.updateAuthor(id, resource, getVersionFromIfMatchHeader(ifMatchValue));
        return ResponseEntity.ok().eTag(Long.toString(author.getVersion())).body(authorViewMapper.toAuthorView(author));
    }

    @Operation(summary = "Partially updates an existing author")
    @PatchMapping(path = "{authorId}")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    public ResponseEntity<AuthorView> partialUpdateAuthor(final WebRequest request,
                                                          @PathVariable("authorId") Long id,
                                                          @Valid @RequestBody final EditAuthorRequest resource) {
        final String ifMatchValue = request.getHeader(IF_MATCH);
        if (ifMatchValue == null || ifMatchValue.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Author author = authorService.partialUpdateAuthor(id, resource, getVersionFromIfMatchHeader(ifMatchValue));
        return ResponseEntity.ok().eTag(Long.toString(author.getVersion())).body(authorViewMapper.toAuthorView(author));
    }

    @NotNull
    private List<BookView> getBookViews(Pageable pageable, List<Book> booksList) {
        int start = Math.min((int) pageable.getOffset(), booksList.size());
        int end = Math.min((start + pageable.getPageSize()), booksList.size());
        List<Book> paginatedBooks = booksList.subList(start, end);

        Page<Book> booksPage = new PageImpl<>(paginatedBooks, pageable, booksList.size());
        return booksPage.map(bookViewMapper::toBookView).getContent();
    }

    private Long getVersionFromIfMatchHeader(final String ifMatchHeader) {
        if (ifMatchHeader.startsWith("\"")) {
            return Long.parseLong(ifMatchHeader.substring(1, ifMatchHeader.length() - 1));
        }
        return Long.parseLong(ifMatchHeader);
    }
}