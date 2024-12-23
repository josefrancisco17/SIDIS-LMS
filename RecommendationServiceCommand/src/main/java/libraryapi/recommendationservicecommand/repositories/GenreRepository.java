package libraryapi.recommendationservicecommand.repositories;

import libraryapi.recommendationservicecommand.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

}
