package libraryapi.readerservicecommand.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import libraryapi.readerservicecommand.model.ReaderPhoto;

public interface ReaderPhotoRepository extends JpaRepository<ReaderPhoto, Long> {
}