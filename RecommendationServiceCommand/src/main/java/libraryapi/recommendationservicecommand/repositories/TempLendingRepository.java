package libraryapi.recommendationservicecommand.repositories;

import libraryapi.recommendationservicecommand.model.Lending;
import libraryapi.recommendationservicecommand.model.TempLending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TempLendingRepository extends JpaRepository<TempLending, Long> {
    Optional<TempLending> findByLendingCode(String lendingCode);
}

