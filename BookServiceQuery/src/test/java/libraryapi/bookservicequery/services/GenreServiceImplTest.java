package libraryapi.bookservicequery.services;

import libraryapi.bookservicequery.model.Genre;
import libraryapi.bookservicequery.repositories.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    private GenreServiceImpl genreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        genreService = new GenreServiceImpl(genreRepository);
    }

    @Test
    void getGenreById() {
        Genre genre = new Genre(1L, "Fiction");
        when(genreRepository.findGenreById(1L)).thenReturn(Optional.of(genre));

        Optional<Genre> result = genreService.getGenreById(1L);

        assertTrue(result.isPresent());
        assertEquals("Fiction", result.get().getName());
    }

    @Test
    void getAllGenres() {
        Genre genre1 = new Genre(1L, "Fiction");
        Genre genre2 = new Genre(2L, "Non-Fiction");

        List<Genre> genres = new ArrayList<>();
        genres.add(genre1);
        genres.add(genre2);

        when(genreRepository.findAll()).thenReturn(genres);

        Iterable<Genre> result = genreService.getAllGenres();

        assertNotNull(result);
        assertTrue(result.iterator().hasNext());
        assertEquals("Fiction", result.iterator().next().getName());
    }
}
