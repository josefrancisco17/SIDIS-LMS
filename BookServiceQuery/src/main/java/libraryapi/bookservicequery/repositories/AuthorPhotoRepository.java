package libraryapi.bookservicequery.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import libraryapi.bookservicequery.model.AuthorPhoto;

public interface AuthorPhotoRepository extends JpaRepository<AuthorPhoto, Long> {}