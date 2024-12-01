package libraryapi.bookservicecommand.services;

import libraryapi.bookservicecommand.model.Genre;
import libraryapi.bookservicecommand.repositories.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreServiceImpl genreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getGenreById_ValidId_ReturnsGenre() {
        // Arrange
        Long genreId = 1L;
        Genre genre = new Genre("Fiction");
        genre.setId(genreId);
        when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

        // Act
        Optional<Genre> resultOptional = genreService.getGenreById(genreId);
        Genre result = resultOptional.orElseThrow(() -> new RuntimeException("[ERROR] Genre not found"));

        // Assert
        assertNotNull(result);
        assertEquals(genreId, result.getId());
        assertEquals("Fiction", result.getName());
        verify(genreRepository).findById(genreId);
    }

    @Test
    void getGenreById_InvalidId_ThrowsNotFoundException() {
        // Arrange
        Long genreId = 999L;  // Um ID que não existe
        when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            genreService.getGenreById(genreId).orElseThrow(() -> new RuntimeException("[ERROR] Genre not found"));
        });

        assertEquals("[ERROR] Genre not found", exception.getMessage());
        verify(genreRepository).findById(genreId);
    }


    @Test
    void getAllGenres_ReturnsGenres() {
        // Arrange
        Genre genre1 = new Genre("Fiction");
        Genre genre2 = new Genre("Science Fiction");
        when(genreRepository.findAll()).thenReturn(Arrays.asList(genre1, genre2));

        // Act
        Iterable<Genre> result = genreService.getAllGenres();

        // Assert
        assertNotNull(result);
        assertEquals(2, ((java.util.Collection<?>) result).size());
        verify(genreRepository).findAll();
    }

    @Test
    void getAllGenres_NoGenresFound_ReturnsEmptyList() {
        // Arrange
        when(genreRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        Iterable<Genre> result = genreService.getAllGenres();

        // Assert
        assertNotNull(result);
        assertEquals(0, ((java.util.Collection<?>) result).size());
        verify(genreRepository).findAll();
    }
}
