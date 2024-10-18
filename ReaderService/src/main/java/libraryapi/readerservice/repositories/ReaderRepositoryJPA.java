package libraryapi.readerservice.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import libraryapi.readerservice.model.Genre;
import libraryapi.readerservice.model.Reader;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReaderRepositoryJPA extends JpaRepository<Reader, Long> {

    @Query("SELECT r FROM Reader r " +
            "WHERE EXISTS (SELECT l FROM Lending l WHERE l.readerId = r.id AND l.lendDate >= :startDate AND l.lendDate <= :endDate) " +
            "ORDER BY (SELECT COUNT(l) FROM Lending l WHERE l.readerId = r.id AND l.lendDate >= :startDate AND l.lendDate <= :endDate) DESC")
    List<Reader> findTopReaders(Pageable pageable, LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM Reader r WHERE r.name = :name")
    Optional<Reader> findReaderByName(String name);

    @Query("SELECT r FROM Reader r WHERE r.id = :id")
    Optional<Reader> findReaderById(Long id);

    @Query("SELECT r FROM Reader r WHERE r.email = :email")
    Optional<Reader> findByEmail(String email);

    @Query("SELECT MAX(r.id) FROM Reader r")
    int findMaxReaderId();
}

