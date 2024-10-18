package libraryapi.readerservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import libraryapi.readerservice.model.ReaderPhoto;

public interface ReaderPhotoRepository extends JpaRepository<ReaderPhoto, Long> {
}