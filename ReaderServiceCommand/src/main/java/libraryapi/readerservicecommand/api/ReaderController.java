package libraryapi.readerservicecommand.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import libraryapi.readerservicecommand.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import libraryapi.readerservicecommand.model.Reader;
import libraryapi.readerservicecommand.services.EditReaderRequest;
import libraryapi.readerservicecommand.services.ReaderServiceImpl;


@Tag(name = "Readers", description = "Endpoints for managing Readers")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/readers")
public class ReaderController {

    private static final String IF_MATCH = "If-Match";
    private final ReaderServiceImpl readerService;
    private final ReaderViewMapper readerViewMapper;

    @Operation(summary = "Creates a new Reader")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ReaderView> createReader(@Valid @RequestPart("reader") final EditReaderRequest resource,
                                                   @RequestPart(value = "photo", required = false) MultipartFile photo) {
        Reader reader = readerService.createReader(resource, photo);

        final var newbarUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(reader.getId().toString())
                .build().toUri();

        return ResponseEntity.created(newbarUri).eTag(Long.toString(reader.getVersion()))
                .body(readerViewMapper.toReaderView(reader));
    }

    @Operation(summary = "Fully replaces an existing reader. If the specified id does not exist does nothing and returns 400.")
    @PutMapping(path = "{readerId}")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    public ResponseEntity<ReaderView> updateReader(final WebRequest request,
                                               @PathVariable("readerId") Long id,
                                               @Valid @RequestBody final EditReaderRequest resource) {
        final String ifMatchValue = request.getHeader(IF_MATCH);
        if (ifMatchValue == null || ifMatchValue.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Reader reader = readerService.updateReader(id, resource, getVersionFromIfMatchHeader(ifMatchValue));
        return ResponseEntity.ok().eTag(Long.toString(reader.getVersion())).body(readerViewMapper.toReaderView(reader));
    }

    @Operation(summary = "Partially updates an existing reader")
    @PatchMapping(path = "{readerId}")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    public ResponseEntity<ReaderView> partialUpdateReader(final WebRequest request,
                                                      @PathVariable("readerId") Long id,
                                                      @Valid @RequestBody final EditReaderRequest resource) {
        final String ifMatchValue = request.getHeader(IF_MATCH);
        if (ifMatchValue == null || ifMatchValue.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Reader reader = readerService.partialUpdateReader(id, resource, getVersionFromIfMatchHeader(ifMatchValue));
        return ResponseEntity.ok().eTag(Long.toString(reader.getVersion())).body(readerViewMapper.toReaderView(reader));
    }

    private Long getVersionFromIfMatchHeader(final String ifMatchHeader) {
        if (ifMatchHeader.startsWith("\"")) {
            return Long.parseLong(ifMatchHeader.substring(1, ifMatchHeader.length() - 1));
        }
        return Long.parseLong(ifMatchHeader);
    }

}