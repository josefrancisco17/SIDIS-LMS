package libraryapi.lendingservice.services;

import libraryapi.lendingservice.api.LendingAvgPerBookView;
import libraryapi.lendingservice.api.LendingAvgPerBookViewMapper;
import libraryapi.lendingservice.api.LendingAvgPerGenrePerMonthView;
import libraryapi.lendingservice.api.LendingAvgPerGenrePerMonthViewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import libraryapi.lendingservice.model.Book;
import libraryapi.lendingservice.model.Genre;
import libraryapi.lendingservice.exceptions.NotFoundException;
import libraryapi.lendingservice.model.Lending;
import libraryapi.lendingservice.repositories.LendingRepository;
import libraryapi.lendingservice.model.Reader;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LendingServiceImpl implements LendingService {

    private final LendingRepository lendingRepository;
    private final LendingAvgPerBookViewMapper lendingAvgPerBookViewMapper;
    private final LendingAvgPerGenrePerMonthViewMapper lendingAvgPerGenrePerMonthViewMapper;

    @Value("${lending.days}")
    private int daysOfLending ;
    @Value("${lending.lateFee}")
    private float lateFee;

    @Autowired
    public LendingServiceImpl(LendingRepository lendingRepository, LendingAvgPerBookViewMapper lendingAvgPerBookViewMapper, LendingAvgPerGenrePerMonthViewMapper lendingAvgPerGenrePerMonthViewMapper) {
        this.lendingRepository = lendingRepository;
        this.lendingAvgPerBookViewMapper = lendingAvgPerBookViewMapper;
        this.lendingAvgPerGenrePerMonthViewMapper = lendingAvgPerGenrePerMonthViewMapper;

    }

    public Optional<Lending> getLending(final Long lendingId) {
        return lendingRepository.findById(lendingId);
    }

    public Page<Lending> getLendings(Pageable pageable) {
        return lendingRepository.findAll(pageable);
    }

    public Iterable<Lending> getAllLendings() {
        return lendingRepository.findAll();
    }

    public List<Lending> getLentBook(Long bookId) {
        return lendingRepository.getLentBook(bookId);
    }

    public Page<Lending> getOverdueLendings(Pageable pageable) {
        return lendingRepository.findOverdueLendings(pageable);
    }

    public double getAverageLendingDuration() {
        List<Lending> allLendings = lendingRepository.findAll();
        long totalDuration = 0;
        int lendingCount = allLendings.size();

        for (Lending lending : allLendings) {
            long duration = ChronoUnit.DAYS.between(lending.getLendDate(), LocalDate.now());
            totalDuration += duration;
        }

        double averageDuration = lendingCount > 0 ? (double) totalDuration / lendingCount : 0;

        return Double.parseDouble(String.format("%.1f", averageDuration));
    }

    public double AveragePerGenreInMonth(LocalDate date) {
        //int numberOfGenres = genreService.getGenres().size();
        int numberOfGenres = 10;
        return lendingRepository.averagePerGenreInMonth(date, numberOfGenres);
    }

    public Lending createLending(final CreateLendingRequest resource) {
        /*
        Optional<Book> book = bookRepository.findById(resource.getBookId());
        if (book.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] Book not found with ID: " + resource.getBookId());
        }

        Optional<Reader> reader = readerRepository.findById(resource.getReaderId());
        Optional<Reader> reader = Optional.empty();
        if (reader.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] Reader not found with ID: " + resource.getReaderId());
        }

        if (!lendingRepository.findOverdueBooksByReaderId(reader.get().getId()).isEmpty()) {
            throw new IllegalArgumentException("[ERROR] Cannot lend book. Reader has overdue books.");
        }

        if (!lendingRepository.findAlreadyLendedBook(reader.get().getId(), book.get().getId()).isEmpty()) {
            throw new IllegalArgumentException("[ERROR] Cannot lend book. Reader has already taken this book.");
        }

        if (lendingRepository.countLentBooksByReaderId(reader.get().getId()) >= 3) {
            throw new IllegalArgumentException("[ERROR] Cannot lend book. Reader has already lent 3 books.");
        }
        LocalDate limitDate = LocalDate.now().plusDays(daysOfLending);

        Lending lending = new Lending();
        String lendingCode = java.time.Year.now().getValue() + "/" + (lendingRepository.findMaxLendingId() + 1);
        lending.setLendingCode(lendingCode);
        lending.setReaderId(resource.getReaderId());
        lending.setBookId(resource.getBookId());
        lending.setLendDate(LocalDate.now());
        lending.setLimitDate(limitDate);
        lending.setReturned(false);
        lending.setFine(0.0f);
        lending.setComment("");
        lending.setDaysOverdue(0);
        lending.setDaysTillReturn((int) ChronoUnit.DAYS.between(LocalDate.now(), limitDate));
        lending.setBookTitle(book.get().getTitle());

        return lendingRepository.save(lending);

         */

        return new Lending("2024/10", (long) 10, (long) 10, "Atomic Habits", LocalDate.of(2024, 5, 3), LocalDate.of(2024, 5, 18), LocalDate.of(2024, 5, 18), true, 0.0f, "");
    }

    public Lending returnBook(final EditLendingRequest resource) {
        Optional<Lending> lending = Optional.empty();
        Lending returnedLending;

        if (resource.getLendingCode() != null) {
            lending = lendingRepository.findByLendingCode(resource.getLendingCode());
        } else if (resource.getId() != null) {
            lending = lendingRepository.findById(resource.getId());
        }

        if (lending.isEmpty()) {
            throw new NotFoundException("[ERROR] Lending not found");
        }

        returnedLending = lending.get();

        if (returnedLending.isReturned()) {
            throw new IllegalArgumentException("[ERROR] Book with lending number: " + resource.getId() + " is already returned.");
        }

        returnedLending.setReturnedDate(LocalDate.now());

        int daysOverdue = (int) ChronoUnit.DAYS.between(returnedLending.getLimitDate(), LocalDate.now());
        float fine;
        if (daysOverdue < 0) {
            daysOverdue = 0;
            fine = 0;
        } else {
            fine = calculateFine(daysOverdue);
        }

        returnedLending.setDaysOverdue(daysOverdue);
        returnedLending.setDaysTillReturn(0);
        returnedLending.setReturned(true);
        returnedLending.setFine(fine);
        returnedLending.setComment(resource.getComment());

        return lendingRepository.save(returnedLending);
    }

    private float calculateFine(long daysOverdue) {
        return daysOverdue * lateFee;
    }
}
