package libraryapi.bookservice.authorManagement.api;

import org.mapstruct.Mapper;
import libraryapi.bookservice.authorManagement.model.Author;

@Mapper(componentModel = "spring")
public abstract class AuthorLentsViewMapper {
    public abstract Iterable<AuthorLentsView> toAuthorLentsView(Iterable<Author> author);
}