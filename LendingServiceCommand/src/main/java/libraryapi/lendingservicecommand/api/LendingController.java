package libraryapi.lendingservicecommand.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import libraryapi.lendingservicecommand.model.Role;
import libraryapi.lendingservicecommand.services.LendingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import libraryapi.lendingservicecommand.model.Lending;
import libraryapi.lendingservicecommand.services.CreateLendingRequest;
import libraryapi.lendingservicecommand.services.EditLendingRequest;


@Tag(name = "Lendings", description = "Endpoints for managing Lendings")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/lendings")
public class LendingController {

    private static final String IF_MATCH = "If-Match";
    private final LendingServiceImpl lendingService;
    private final LendingViewMapper lendingViewMapper;

    @Operation(summary = "Creates a new Lending")
    @PostMapping
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LendingView> createLending(@Valid @RequestBody final CreateLendingRequest resource) {
        Lending lending = lendingService.createLending(resource);

        final var newbarUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(lending.getId().toString())
                .build().toUri();

        return ResponseEntity.created(newbarUri).eTag(Long.toString(lending.getVersion()))
                .body(lendingViewMapper.toLendingView(lending));
    }

    @Operation(summary = "Saves a new Lending created in another instance")
    @PostMapping("/internal")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LendingView> createInternalLending(@Valid @RequestBody Lending lending) {
        Lending savedLending = lendingService.manageInternalLending(lending);

        final var newbarUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(savedLending.getId().toString())
                .build().toUri();

        return ResponseEntity.created(newbarUri).eTag(Long.toString(savedLending.getVersion()))
                .body(lendingViewMapper.toLendingView(savedLending));
    }

    @Operation(summary = "Return a Book")
    @PostMapping("/return")
    @RolesAllowed({Role.ADMIN, Role.READER})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LendingView> returnBook(@Valid @RequestBody final EditLendingRequest resource) {
        Lending lending = lendingService.returnBook(resource);

        final var newbarUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(lending.getId().toString())
                .build().toUri();

        return ResponseEntity.created(newbarUri).eTag(Long.toString(lending.getVersion()))
                .body(lendingViewMapper.toLendingView(lending));
    }

    @Operation(summary = "Returns a Lending that was returned in another instance")
    @PostMapping("/internal/return")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LendingView> returnInternalBook(@Valid @RequestBody Lending lending) {
        Lending savedLending = lendingService.manageInternalLending(lending);

        final var newbarUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(savedLending.getId().toString())
                .build().toUri();

        return ResponseEntity.created(newbarUri).eTag(Long.toString(savedLending.getVersion()))
                .body(lendingViewMapper.toLendingView(savedLending));
    }
}


