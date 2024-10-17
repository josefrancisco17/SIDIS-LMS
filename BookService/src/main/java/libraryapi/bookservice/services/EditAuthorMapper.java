package libraryapi.bookservice.services;

import org.mapstruct.Mapper;
import libraryapi.bookservice.model.Author;

@Mapper(componentModel = "spring")
public abstract class EditAuthorMapper {
    public abstract Author create(EditAuthorRequest request);
}