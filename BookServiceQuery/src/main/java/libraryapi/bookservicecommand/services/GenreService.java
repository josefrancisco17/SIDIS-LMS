package libraryapi.bookservicecommand.services;

import libraryapi.bookservicecommand.model.Genre;

import java.util.Optional;

public interface GenreService {
    Optional<Genre> getGenreById(final Long id);
    Iterable<Genre> getAllGenres();
}
