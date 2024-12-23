package libraryapi.recommendationservicecommand.repositories;

import libraryapi.recommendationservicecommand.model.ReaderPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReaderPhotoRepository extends JpaRepository<ReaderPhoto, Long> {
}