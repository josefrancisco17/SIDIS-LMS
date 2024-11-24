package libraryapi.readerservicequery.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import libraryapi.readerservicequery.model.Role;
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
import libraryapi.readerservicequery.exceptions.NotFoundException;
import libraryapi.readerservicequery.model.Reader;
import libraryapi.readerservicequery.model.ReaderPhoto;
import libraryapi.readerservicequery.services.EditReaderRequest;
import libraryapi.readerservicequery.services.ReaderServiceImpl;


@Tag(name = "Readers", description = "Endpoints for managing Readers")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/readers")
public class ReaderController {

    private static final String IF_MATCH = "If-Match";
    private final ReaderServiceImpl readerService;
    private final ReaderViewMapper readerViewMapper;
    private final ReaderProfileViewMapper readerProfileViewMapper ;
    private final BookViewMapper bookViewMapper;

    @GetMapping("/{readerId}")
    @RolesAllowed({Role.READER, Role.LIBRARIAN, Role.ADMIN})
    @ApiResponse(description = "Success", content = { @Content(mediaType = "application/json",
            schema = @Schema(implementation = ReaderProfileView.class)) })
    public ResponseEntity<ReaderProfileView> getReader(
            @PathVariable("readerId") Long id,
            HttpServletRequest request) {

        var ReaderProfileView = readerProfileViewMapper.toReaderProfileView(readerService.getReader(id, request).orElseThrow(() -> new NotFoundException(Reader.class, id)));
        return ResponseEntity.ok().body(ReaderProfileView);
    }

    @Operation(summary = "Gets Readers")
    @GetMapping
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    @ApiResponse(description = "Success", responseCode = "200", content = { @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ReaderView.class))) })
    public Iterable<ReaderView> getReaders(@RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                                           @RequestParam(value = "email", required = false) String email,
                                           @RequestParam(value = "name", required = false) String name,
                                           @RequestParam(defaultValue = "0", required = false) int page,
                                           @RequestParam(defaultValue = "100", required = false) int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Reader> readersPage;

        if (name != null) {
            readersPage = readerService.getReadersByName(name, pageable);
        } else if (phoneNumber != null && email != null) {
            readersPage = readerService.getReadersByPhoneNumberAndEmail(phoneNumber, email, pageable);
        } else if (phoneNumber != null) {
            readersPage = readerService.getReadersByPhoneNumber(phoneNumber, pageable);
        } else if (email != null) {
            readersPage = readerService.getReadersByEmail(email, pageable);
        } else {
            readersPage = readerService.getReaders(pageable);
        }

        return  readersPage.map(readerViewMapper::toReaderView).getContent();
    }

    @Operation(summary = "Gets all Readers for other services")
    @GetMapping("/internal")
    @ApiResponse(description = "Success", responseCode = "200", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Reader.class)))
    })
    public Iterable<Reader> getAllReaders() {
        return readerService.getAllReaders();
    }

    @Operation(summary = "Gets the Top 5 Readers")
    @GetMapping("/top-readers")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    @ApiResponse(description = "Success", content = { @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ReaderView.class))) })
    public Iterable<ReaderView> getTopReaders() {
        return readerViewMapper.toReaderView(readerService.getTopReaders());
    }

    @Operation(summary = "Gets book suggestions based on reader's interest list")
    @GetMapping("/{readerId}/suggestions")
    @RolesAllowed({Role.READER, Role.LIBRARIAN, Role.ADMIN})
    public Iterable<BookView> getSuggestedBooks(@PathVariable("readerId") Long readerId,
                                                @RequestParam(defaultValue = "0", required = false) int page,
                                                @RequestParam(defaultValue = "100", required = false) int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookViewMapper.toBookView(readerService.getSuggestedBooks(readerId, pageable));
    }

    @Operation(summary = "Downloads a cover of a reader by id")
    @GetMapping("/{readerId}/photo")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    public ResponseEntity<Resource> getReaderPhoto(@PathVariable("readerId") final String readerId,
                                                 final HttpServletRequest request) {

        ReaderPhoto readerPhoto = readerService.getReaderPhoto(readerId);

        final Resource resource = new ByteArrayResource(readerPhoto.getImage());

        String contentType = readerPhoto.getContentType();

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}