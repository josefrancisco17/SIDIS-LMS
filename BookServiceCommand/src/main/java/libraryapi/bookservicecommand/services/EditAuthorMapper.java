package libraryapi.bookservicecommand.services;

import org.mapstruct.Mapper;
import libraryapi.bookservicecommand.model.Author;

@Mapper(componentModel = "spring")
public abstract class EditAuthorMapper {
    public abstract Author create(EditAuthorRequest request);
}