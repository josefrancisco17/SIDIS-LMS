package libraryapi.bookservicequery.api;

import libraryapi.bookservicequery.model.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AuthorLentsViewMapper {
    public abstract Iterable<AuthorLentsView> toAuthorLentsView(Iterable<Author> author);
}