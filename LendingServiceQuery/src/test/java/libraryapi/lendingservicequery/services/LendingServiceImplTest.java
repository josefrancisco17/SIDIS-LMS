package libraryapi.lendingservicequery.services;

import libraryapi.lendingservicequery.model.Genre;
import libraryapi.lendingservicequery.model.Lending;
import libraryapi.lendingservicequery.repositories.GenreRepository;
import libraryapi.lendingservicequery.repositories.LendingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LendingServiceImplTest {

    private LendingRepository lendingRepository;
    private GenreRepository genreRepository;
    private LendingServiceImpl lendingService;

    @BeforeEach
    void setUp() {
        lendingRepository = mock(LendingRepository.class);
        genreRepository = mock(GenreRepository.class);
        lendingService = new LendingServiceImpl(lendingRepository, genreRepository);
    }

    @Test
    void getLending() {
        Long lendingId = 1L;
        Lending lending = new Lending();
        lending.setId(lendingId);

        when(lendingRepository.findById(lendingId)).thenReturn(Optional.of(lending));

        Optional<Lending> result = lendingService.getLending(lendingId);

        assertTrue(result.isPresent());
        assertEquals(lendingId, result.get().getId());
    }

    @Test
    void getLendings() {
        PageRequest pageable = PageRequest.of(0, 10);
        Lending lending = new Lending();
        lending.setId(1L);
        List<Lending> lendingsList = Arrays.asList(lending);
        Page<Lending> page = mock(Page.class);
        when(page.getContent()).thenReturn(lendingsList);
        when(lendingRepository.findAll(pageable)).thenReturn(page);

        Page<Lending> result = lendingService.getLendings(pageable);

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
    }

    @Test
    void getAllLendings() {
        Lending lending = new Lending();
        lending.setId(1L);
        List<Lending> lendingsList = Arrays.asList(lending);
        when(lendingRepository.findAll()).thenReturn(lendingsList);

        Iterable<Lending> result = lendingService.getAllLendings();

        assertNotNull(result);
        assertTrue(result.iterator().hasNext());
    }

    @Test
    void getLentBook() {
        Long bookId = 1L;
        Lending lending = new Lending();
        lending.setId(1L);
        List<Lending> lendingsList = Arrays.asList(lending);
        when(lendingRepository.getLentBook(bookId)).thenReturn(lendingsList);

        List<Lending> result = lendingService.getLentBook(bookId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookId, result.get(0).getId());
    }

    @Test
    void getOverdueLendings() {
        PageRequest pageable = PageRequest.of(0, 10);
        Lending lending = new Lending();
        lending.setId(1L);
        List<Lending> lendingsList = Arrays.asList(lending);
        Page<Lending> page = mock(Page.class);
        when(page.getContent()).thenReturn(lendingsList);
        when(lendingRepository.findOverdueLendings(pageable)).thenReturn(page);

        Page<Lending> result = lendingService.getOverdueLendings(pageable);

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
    }

    @Test
    void getAverageLendingDuration() {
        Lending lending1 = new Lending();
        lending1.setLendDate(LocalDate.now().minusDays(5));
        Lending lending2 = new Lending();
        lending2.setLendDate(LocalDate.now().minusDays(10));
        List<Lending> lendingsList = Arrays.asList(lending1, lending2);
        when(lendingRepository.findAll()).thenReturn(lendingsList);

        double result = lendingService.getAverageLendingDuration();

        assertEquals(7.5, result, 0.1);
    }

    @Test
    void averagePerGenreInMonth() {
        LocalDate date = LocalDate.of(2024, 11, 1);
        Genre genre1 = new Genre();
        genre1.setId(1L);
        Genre genre2 = new Genre();
        genre2.setId(2L);
        List<Genre> genres = Arrays.asList(genre1, genre2);
        Lending lending = new Lending();
        lending.setLendDate(LocalDate.of(2024, 11, 15));
        List<Lending> lendingsList = Arrays.asList(lending);

        when(genreRepository.findAll()).thenReturn(genres);
        when(lendingRepository.findAll()).thenReturn(lendingsList);

        double result = lendingService.AveragePerGenreInMonth(date);

        assertEquals(0.5, result, 0.1);
    }
}
