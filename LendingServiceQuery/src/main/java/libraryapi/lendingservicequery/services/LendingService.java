package libraryapi.lendingservicequery.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import libraryapi.lendingservicequery.model.Lending;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LendingService {
    Optional<Lending> getLending(Long lendingId);
    Page<Lending> getLendings(Pageable pageable);
    Iterable<Lending> getAllLendings();
    List<Lending> getLentBook(Long bookId);
    Page<Lending> getOverdueLendings(Pageable pageable);
    double getAverageLendingDuration();
    double AveragePerGenreInMonth(LocalDate date);
    Lending createLending(CreateLendingRequest resource);
    Lending manageInternalLending(Lending lending);
    Lending returnBook(EditLendingRequest resource);
}
