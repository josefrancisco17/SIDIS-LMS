package libraryapi.readerservice.readerManagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import libraryapi.readerservice.readerManagement.model.ReaderPhoto;

public interface ReaderPhotoRepository extends JpaRepository<ReaderPhoto, Long> {
}