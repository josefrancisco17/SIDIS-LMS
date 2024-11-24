package libraryapi.bookservicequery.repositories;

import libraryapi.bookservicequery.model.Lending;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LendingRepository extends JpaRepository<Lending, Long> {

}
