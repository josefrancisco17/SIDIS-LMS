package libraryapi.bookservicecommand.services;
import static org.junit.jupiter.api.Assertions.assertThrows;
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



    /*@Test
    void getGenreById_ValidId_ReturnsGenre() {
        Long genreId = 1L;
        Genre genre = new Genre("Fiction");
        genre.setId(genreId);

        when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

        Optional<Genre> resultOptional = genreService.getGenreById(genreId);

        assertTrue(resultOptional.isPresent(), "[ERROR] Genre should be found!");

        Genre result = resultOptional.get();

        assertNotNull(result);  // Verifica que o resultado não é nulo
        assertEquals(genreId, result.getId());  // Verifica que o ID está correto
        assertEquals("Fiction", result.getName());  // Verifica que o nome está correto

        verify(genreRepository).findById(genreId);
    } */


    /*@Test
    void getGenreById_InvalidId_ThrowsException() {
        Long genreId = 1L;

        when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            genreService.getGenreById(genreId);
        });

        assertEquals("[ERROR] Genre not found", exception.getMessage());
    }*/

    @Test
    void getAllGenres_ReturnsGenres() {
        Genre genre1 = new Genre("Fiction");
        Genre genre2 = new Genre("Science Fiction");
        when(genreRepository.findAll()).thenReturn(Arrays.asList(genre1, genre2));

        Iterable<Genre> result = genreService.getAllGenres();

        assertNotNull(result);
        assertEquals(2, ((java.util.Collection<?>) result).size());
        verify(genreRepository).findAll();
    }

    @Test
    void getAllGenres_NoGenresFound_ReturnsEmptyList() {
        when(genreRepository.findAll()).thenReturn(Arrays.asList());

        Iterable<Genre> result = genreService.getAllGenres();

        assertNotNull(result);
        assertEquals(0, ((java.util.Collection<?>) result).size());
        verify(genreRepository).findAll();
    }
}
