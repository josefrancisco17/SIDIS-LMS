package libraryapi.lendingservice.services;

import libraryapi.lendingservice.model.Genre;
import libraryapi.lendingservice.rabbitMQ.producer.Sender;
import libraryapi.lendingservice.repositories.BookRepositoryHTTP;
import libraryapi.lendingservice.repositories.LendingRepositoryHTTP;
import libraryapi.lendingservice.repositories.ReaderRepositoryHTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import libraryapi.lendingservice.model.Book;
import libraryapi.lendingservice.exceptions.NotFoundException;
import libraryapi.lendingservice.model.Lending;
import libraryapi.lendingservice.repositories.LendingRepository;
import libraryapi.lendingservice.model.Reader;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class LendingServiceImpl implements LendingService {

    private final LendingRepository lendingRepository;
    private final LendingRepositoryHTTP lendingRepositoryHTTP;
    private final BookRepositoryHTTP bookRepositoryHTTP;
    private final ReaderRepositoryHTTP readerRepositoryHTTP;

    @Value("${lending.days}")
    private int daysOfLending ;
    @Value("${lending.lateFee}")
    private float lateFee;

    @Autowired
    private Sender sender;

    @Autowired
    public LendingServiceImpl(LendingRepository lendingRepository, LendingRepositoryHTTP lendingRepositoryHTTP, BookRepositoryHTTP bookRepositoryHTTP, ReaderRepositoryHTTP readerRepositoryHTTP ) {
        this.lendingRepository = lendingRepository;
        this.lendingRepositoryHTTP = lendingRepositoryHTTP;
        this.bookRepositoryHTTP = bookRepositoryHTTP;
        this.readerRepositoryHTTP = readerRepositoryHTTP;
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
        //int numberOfGenres = bookRepositoryHTTP.getAllGenres().size();
        List<Genre> genres =  new ArrayList<>();
        try {
            genres = sender.getGenres();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int numberOfGenres = genres.size();
        List<Lending> lendings = lendingRepository.findAll();
        long count = lendings.stream()
                .filter(l -> l.getLendDate().getMonth() == date.getMonth() &&
                        l.getLendDate().getYear() == date.getYear())
                .count();

        return (count == 0 || numberOfGenres == 0) ? 0 : (double) count / numberOfGenres;
    }


    public Lending createLending(final CreateLendingRequest resource) {
        //List<Book> books = bookRepositoryHTTP.getAllBooks();
        List<Book> books =  new ArrayList<>();
        try {
            books = sender.getBooks();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(books);
        Book book = new Book();
        for (Book b : books) {
            if (Objects.equals(b.getId(), resource.getBookId())) {
                book = b;
            }
        }
        if (book.getId() == null) {
            throw new IllegalArgumentException("[ERROR] Book not found with ID: " + resource.getBookId());
        }

        //List<Reader> readers = readerRepositoryHTTP.getAllReaders();
        List<Reader> readers =  new ArrayList<>();
        try {
            readers = sender.getReaders();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Reader reader = new Reader();
        for (Reader r : readers) {
            if (Objects.equals(r.getId(), resource.getReaderId())) {
                reader = r;
            }
        }
        if (reader.getId() == null) {
            throw new IllegalArgumentException("[ERROR] Reader not found with ID: " + resource.getReaderId());
        }

        if (!lendingRepository.findOverdueBooksByReaderId(reader.getId()).isEmpty()) {
            throw new IllegalArgumentException("[ERROR] Cannot lend book. Reader has overdue books.");
        }

        if (!lendingRepository.findAlreadyLendedBook(reader.getId(), book.getId()).isEmpty()) {
            throw new IllegalArgumentException("[ERROR] Cannot lend book. Reader has already taken this book.");
        }

        if (lendingRepository.countLentBooksByReaderId(reader.getId()) >= 3) {
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
        lending.setBookTitle(book.getTitle());

        //lendingRepositoryHTTP.manageInternalLending(lending);
        try {
            sender.sendSyncLending(lending);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lendingRepository.save(lending);
    }

    public Lending manageInternalLending(Lending lending) {
        return lendingRepository.save(lending);
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

        //lendingRepositoryHTTP.manageInternalLending(returnedLending);
        try {
            sender.sendSyncLending(returnedLending);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lendingRepository.save(returnedLending);
    }

    private float calculateFine(long daysOverdue) {
        return daysOverdue * lateFee;
    }
}
