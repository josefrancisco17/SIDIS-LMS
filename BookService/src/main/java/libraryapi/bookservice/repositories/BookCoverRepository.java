package libraryapi.bookservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import libraryapi.bookservice.model.BookCover;

@Repository
public interface BookCoverRepository extends JpaRepository<BookCover, Long> {
}
