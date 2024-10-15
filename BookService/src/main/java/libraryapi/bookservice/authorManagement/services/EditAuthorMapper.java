package libraryapi.bookservice.authorManagement.services;

import org.mapstruct.Mapper;
import libraryapi.bookservice.authorManagement.model.Author;

@Mapper(componentModel = "spring")
public abstract class EditAuthorMapper {
    public abstract Author create(EditAuthorRequest request);
}