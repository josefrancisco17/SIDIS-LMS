package libraryapi.lendingservicequery.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import libraryapi.lendingservicequery.model.Lending;

import java.time.LocalDate;
import java.util.Optional;

public interface LendingService {
    Optional<Lending> getLending(Long lendingId);
    Page<Lending> getLendings(Pageable pageable);
    Iterable<Lending> getAllLendings();
    Page<Lending> getOverdueLendings(Pageable pageable);
    double getAverageLendingDuration();
    double AveragePerGenreInMonth(LocalDate date);
}
