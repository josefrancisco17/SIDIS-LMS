package libraryapi.lendingservicecommand.repositories;

import libraryapi.lendingservicecommand.model.ReaderPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReaderPhotoRepository extends JpaRepository<ReaderPhoto, Long> {
}