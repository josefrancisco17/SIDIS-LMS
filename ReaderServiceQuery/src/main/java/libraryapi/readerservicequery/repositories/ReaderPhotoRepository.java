package libraryapi.readerservicequery.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import libraryapi.readerservicequery.model.ReaderPhoto;

public interface ReaderPhotoRepository extends JpaRepository<ReaderPhoto, Long> {
}