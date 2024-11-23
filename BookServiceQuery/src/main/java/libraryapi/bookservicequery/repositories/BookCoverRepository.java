package libraryapi.bookservicequery.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import libraryapi.bookservicequery.model.BookCover;

@Repository
public interface BookCoverRepository extends JpaRepository<BookCover, Long> {
}
