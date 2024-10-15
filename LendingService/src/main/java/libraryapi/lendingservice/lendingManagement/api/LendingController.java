package libraryapi.lendingservice.lendingManagement.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import psoftg2.libraryapi.authorManagement.model.Author;
import psoftg2.libraryapi.bookManagement.services.GenreServiceImpl;
import psoftg2.libraryapi.exceptions.NotFoundException;
import libraryapi.lendingservice.lendingManagement.model.Lending;
import libraryapi.lendingservice.lendingManagement.services.CreateLendingRequest;
import libraryapi.lendingservice.lendingManagement.services.EditLendingRequest;
import libraryapi.lendingservice.lendingManagement.services.LendingServiceImpl;
import psoftg2.libraryapi.userManagement.model.Role;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "Lendings", description = "Endpoints for managing Lendings")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/lendings")
public class LendingController {

    private static final String IF_MATCH = "If-Match";
    private final LendingServiceImpl lendingService;
    private final LendingViewMapper lendingViewMapper;
    private final GenreServiceImpl genreService;

    @Operation(summary = "Gets all Lendings")
    @GetMapping
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    @ApiResponse(description = "Success", responseCode = "200", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LendingView.class)))})
    public List<LendingView> getLendingsPage(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "100", required = false) int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Lending> lendingsPage = lendingService.getLendings(pageable);
        return lendingsPage.map(lendingViewMapper::toLendingView).getContent();
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

    @Operation(summary = "Gets the number of lendings per month for the last 12 months, broken down by genre")
    @GetMapping("/lending-genre/{genreId}")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    public Map<Integer, Long> getAveragePerGenreInMonth(@PathVariable("genreId") Long genreId) {
        final var genre = genreService.getGenreById(genreId).orElseThrow(() -> new NotFoundException(Author.class, genreId));
        return lendingService.numberOfLendingsPerMonthByGenre(genre);
    }

    @Operation(summary = "Gets the average number of lending per genre of a certain month\n")
    @GetMapping("/average-per-genre/{date}")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    public double getAveragePerGenreInMonth(@PathVariable("date") LocalDate date) {
        int numberOfGenres = genreService.getGenres().size();
        return lendingService.AveragePerGenreInMonth(date, numberOfGenres);
    }

    @Operation(summary = "Gets average lending duration per book")
    @GetMapping("/average-per-book")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    public Iterable<LendingAvgPerBookView> getAverageLendingDurationPerBook() {
        return lendingService.getAverageLendingDurationPerBook();
    }

    @Operation(summary = "Gets average lending duration per genre per month for a certain period")
    @GetMapping("/average-duration-per-genre")
    @RolesAllowed({Role.LIBRARIAN, Role.ADMIN})
    public Iterable<LendingAvgPerGenrePerMonthView> getAverageLendingDurationPerGenrePerMonth(
            @RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate) {
        return lendingService.getAverageLendingDurationPerGenrePerMonth(startDate, endDate);
    }

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
    
    private Long getVersionFromIfMatchHeader(final String ifMatchHeader) {
        if (ifMatchHeader.startsWith("\"")) {
            return Long.parseLong(ifMatchHeader.substring(1, ifMatchHeader.length() - 1));
        }
        return Long.parseLong(ifMatchHeader);
    }
}


