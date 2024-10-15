package libraryapi.readerservice.bookManagement.repositories;

import libraryapi.readerservice.bookManagement.model.BookCover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCoverRepository extends JpaRepository<BookCover, Long> {
}
