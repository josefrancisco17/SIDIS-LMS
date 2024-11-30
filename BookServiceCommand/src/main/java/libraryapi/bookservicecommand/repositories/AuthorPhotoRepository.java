package libraryapi.bookservicecommand.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import libraryapi.bookservicecommand.model.AuthorPhoto;

public interface AuthorPhotoRepository extends JpaRepository<AuthorPhoto, Long> {}