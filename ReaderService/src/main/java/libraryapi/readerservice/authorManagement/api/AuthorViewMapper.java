package libraryapi.readerservice.authorManagement.api;

import libraryapi.readerservice.authorManagement.api.AuthorView;
import libraryapi.readerservice.authorManagement.model.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AuthorViewMapper {
    public abstract AuthorView toAuthorView(Author author);
    public abstract Iterable<AuthorView> toAuthorView(Iterable<Author> author);
}