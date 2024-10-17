package libraryapi.bookservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import libraryapi.bookservice.model.AuthorPhoto;

public interface AuthorPhotoRepository extends JpaRepository<AuthorPhoto, Long> {}