package libraryapi.bookservice.bookManagement.services;

import libraryapi.bookservice.bookManagement.model.Genre;

import java.util.Optional;

public interface GenreService {
    Optional<Genre> getGenreById(final Long id);
    Iterable<Genre> getGenres();
}
