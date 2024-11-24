package libraryapi.lendingservicequery.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import libraryapi.lendingservicequery.model.Role;
import libraryapi.lendingservicequery.services.LendingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import libraryapi.lendingservicequery.exceptions.NotFoundException;
import libraryapi.lendingservicequery.model.Lending;
import libraryapi.lendingservicequery.services.CreateLendingRequest;
import libraryapi.lendingservicequery.services.EditLendingRequest;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Lendings", description = "Endpoints for managing Lendings")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/lendings")
public class LendingController {

    private static final String IF_MATCH = "If-Match";
    private final LendingServiceImpl lendingService;
    private final LendingViewMapper lendingViewMapper;

    @Operation(summary = "Gets all Lendings")
    @GetMapping
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    @ApiResponse(description = "Success", responseCode = "200", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LendingView.class)))})
    public List<LendingView> getLendings(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "100", required = false) int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Lending> lendingsPage = lendingService.getLendings(pageable);
        return lendingsPage.map(lendingViewMapper::toLendingView).getContent();
    }

    @Operation(summary = "Gets all Lendings for other services")
    @GetMapping("/internal")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    @ApiResponse(description = "Success", responseCode = "200", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Lending.class)))})
    public Iterable<Lending> getAllLendings() {
        return lendingService.getAllLendings();
    }


    @Operation(summary = "Gets a specific Lending")
    @GetMapping("/{lendingId}")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN, Role.READER})
    public ResponseEntity<LendingView> getLending(@PathVariable("lendingId") Long id) {
        final var lending = lendingService.getLending(id).orElseThrow(() -> new NotFoundException(Lending.class, id));

        return ResponseEntity.ok().eTag(Long.toString(lending.getVersion())).body(lendingViewMapper.toLendingView(lending));
    }

    @Operation(summary = "Gets a list of overdue lending sorted by their tardiness")
    @GetMapping("/overdue")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    public List<LendingView> getOverdue(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "100", required = false) int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Lending> overdueLendingsPage = lendingService.getOverdueLendings(pageable);
        return overdueLendingsPage.map(lendingViewMapper::toLendingView).getContent();
    }

    @Operation(summary = "Gets average lending duration")
    @GetMapping("/average")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    public double getAverageLendingDuration() {
        return lendingService.getAverageLendingDuration();
    }

    @Operation(summary = "Gets the average number of lending per genre of a certain month\n")
    @GetMapping("/average-per-genre/{date}")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    public double getAveragePerGenreInMonth(@PathVariable("date") LocalDate date) {
        return lendingService.AveragePerGenreInMonth(date);
    }
}


