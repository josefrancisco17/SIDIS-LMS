package libraryapi.recommendationservicecommand.repositories;

import libraryapi.recommendationservicecommand.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    Optional<Recommendation> findByLendingCode(String lendingCode);
}
