package libraryapi.lendingservicequery.services;

import libraryapi.lendingservicequery.model.Genre;
import libraryapi.lendingservicequery.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import libraryapi.lendingservicequery.model.Lending;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class LendingServiceImpl implements LendingService {

    private final LendingRepository lendingRepository;
    private final GenreRepository genreRepository;

    @Autowired
    public LendingServiceImpl(LendingRepository lendingRepository, GenreRepository genreRepository) {
        this.lendingRepository = lendingRepository;
        this.genreRepository = genreRepository;
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
        List<Genre> genres =  genreRepository.findAll();
        int numberOfGenres = genres.size();
        List<Lending> lendings = lendingRepository.findAll();
        long count = lendings.stream()
                .filter(l -> l.getLendDate().getMonth() == date.getMonth() &&
                        l.getLendDate().getYear() == date.getYear())
                .count();

        return (count == 0 || numberOfGenres == 0) ? 0 : (double) count / numberOfGenres;
    }
}
