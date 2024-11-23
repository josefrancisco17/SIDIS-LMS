package libraryapi.bookservicequery.services;

import org.mapstruct.Mapper;
import libraryapi.bookservicequery.model.Author;

@Mapper(componentModel = "spring")
public abstract class EditAuthorMapper {
    public abstract Author create(EditAuthorRequest request);
}