package libraryapi.readerservice.authorManagement.api;

import libraryapi.readerservice.authorManagement.api.AuthorLentsView;
import libraryapi.readerservice.authorManagement.model.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AuthorLentsViewMapper {
    public abstract Iterable<AuthorLentsView> toAuthorLentsView(Iterable<Author> author);
}