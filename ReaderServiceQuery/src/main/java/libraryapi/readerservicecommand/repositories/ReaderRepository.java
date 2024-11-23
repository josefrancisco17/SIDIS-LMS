package libraryapi.readerservicecommand.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import libraryapi.readerservicecommand.model.Reader;

import java.util.Optional;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {

    @Query("SELECT r FROM Reader r WHERE r.name = :name")
    Optional<Reader> findReaderByName(String name);

    @Query("SELECT r FROM Reader r WHERE r.id = :id")
    Optional<Reader> findReaderById(Long id);

    @Query("SELECT r FROM Reader r WHERE r.email = :email")
    Optional<Reader> findByEmail(String email);

    @Query("SELECT MAX(r.id) FROM Reader r")
    int findMaxReaderId();
}

