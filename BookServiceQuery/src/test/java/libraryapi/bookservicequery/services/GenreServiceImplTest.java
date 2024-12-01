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
        // Inicializa os mocks
        MockitoAnnotations.openMocks(this);

        // Inicializa a implementação do serviço, injetando o repositório mockado
        genreService = new GenreServiceImpl(genreRepository);
    }

    @Test
    void getGenreById() {
        // Configura o mock para o comportamento esperado
        Genre genre = new Genre(1L, "Fiction");
        when(genreRepository.findGenreById(1L)).thenReturn(Optional.of(genre));

        // Chama o método a ser testado
        Optional<Genre> result = genreService.getGenreById(1L);

        // Verifica se o retorno é correto
        assertTrue(result.isPresent());
        assertEquals("Fiction", result.get().getName());
    }

    @Test
    void getAllGenres() {
        // Configura o mock para o comportamento esperado
        Genre genre1 = new Genre(1L, "Fiction");
        Genre genre2 = new Genre(2L, "Non-Fiction");

        // Usando ArrayList para converter Iterable para List
        List<Genre> genres = new ArrayList<>();
        genres.add(genre1);
        genres.add(genre2);

        when(genreRepository.findAll()).thenReturn(genres);

        // Chama o método a ser testado
        Iterable<Genre> result = genreService.getAllGenres();

        // Verifica se o retorno é correto
        assertNotNull(result);
        assertTrue(result.iterator().hasNext());
        assertEquals("Fiction", result.iterator().next().getName());
    }
}
