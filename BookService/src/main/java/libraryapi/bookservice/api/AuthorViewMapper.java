package libraryapi.bookservice.api;

import libraryapi.bookservice.model.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AuthorViewMapper {
    public abstract AuthorView toAuthorView(Author author);
    public abstract Iterable<AuthorView> toAuthorView(Iterable<Author> author);
}