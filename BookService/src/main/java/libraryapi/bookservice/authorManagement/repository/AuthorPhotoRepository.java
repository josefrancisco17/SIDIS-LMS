package libraryapi.bookservice.authorManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import libraryapi.bookservice.authorManagement.model.AuthorPhoto;

public interface AuthorPhotoRepository extends JpaRepository<AuthorPhoto, Long> {}