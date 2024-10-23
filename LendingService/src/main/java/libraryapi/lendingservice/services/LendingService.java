package libraryapi.lendingservice.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import libraryapi.lendingservice.model.Lending;

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
    Lending createInternalLending(Lending lending);
    Lending returnBook(EditLendingRequest resource);
    Lending returnInternalBook(Lending lending);
}
