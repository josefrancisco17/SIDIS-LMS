package libraryapi.lendingservicequery.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import libraryapi.lendingservicequery.model.Lending;

import java.util.List;
import java.util.Optional;

@Repository
public interface LendingRepository extends JpaRepository<Lending, Long> {
    @Query("SELECT COUNT(l) FROM Lending l WHERE l.readerId = :readerId AND l.returned = FALSE ")
    int countLentBooksByReaderId(Long readerId);

    @Query("SELECT l FROM Lending l WHERE l.readerId = :readerId AND l.returned = false AND l.limitDate < CURRENT_DATE")
    List<Lending> findOverdueBooksByReaderId(Long readerId);

    @Query("SELECT l FROM Lending l WHERE l.lendingCode = :lendingCode")
    Optional<Lending> findByLendingCode(String lendingCode);

    @Query("SELECT MAX(l.id) FROM Lending l")
    int findMaxLendingId();

    @Query("SELECT l FROM Lending l WHERE l.returned = false AND l.limitDate < CURRENT_DATE()")
    Page<Lending> findOverdueLendings(Pageable pageable);

    @Query("SELECT l FROM Lending l WHERE l.readerId = :readerId AND l.bookId = :bookId AND l.returned = FALSE")
    List<Lending> findAlreadyLendedBook(Long readerId, Long bookId);

    @Query("SELECT l FROM Lending l WHERE l.bookId = :id")
    List<Lending> getLentBook(Long id);
}
