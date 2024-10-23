package libraryapi.bookservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import libraryapi.bookservice.model.Genre;
import libraryapi.bookservice.repositories.GenreRepository;

import java.util.Optional;

@Service
public class GenreServiceImpl implements GenreService{
    private final GenreRepository genreRepository;

    @Autowired
    public GenreServiceImpl(GenreRepository genreRepository){
        this.genreRepository = genreRepository;
    }

    public Optional<Genre> getGenreById(final Long id) {
        return genreRepository.findGenreById(id);
    }

    public Iterable<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

}
