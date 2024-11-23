package libraryapi.bookservicequery.services;

import libraryapi.bookservicequery.model.Genre;

import java.util.Optional;

public interface GenreService {
    Optional<Genre> getGenreById(final Long id);
    Iterable<Genre> getAllGenres();
}
