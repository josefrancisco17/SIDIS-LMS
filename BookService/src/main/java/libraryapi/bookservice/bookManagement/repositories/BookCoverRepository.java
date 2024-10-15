package libraryapi.bookservice.bookManagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import libraryapi.bookservice.bookManagement.model.BookCover;

@Repository
public interface BookCoverRepository extends JpaRepository<BookCover, Long> {
}
