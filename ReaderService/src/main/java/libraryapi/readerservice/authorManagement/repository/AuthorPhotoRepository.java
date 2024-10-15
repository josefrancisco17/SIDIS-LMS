package libraryapi.readerservice.authorManagement.repository;

import libraryapi.readerservice.authorManagement.model.AuthorPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorPhotoRepository extends JpaRepository<AuthorPhoto, Long> {}