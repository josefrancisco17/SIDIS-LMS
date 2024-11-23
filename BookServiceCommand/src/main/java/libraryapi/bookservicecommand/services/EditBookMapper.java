package libraryapi.bookservicecommand.services;

import org.mapstruct.Mapper;
import libraryapi.bookservicecommand.model.Book;

@Mapper(componentModel = "spring")
public abstract class EditBookMapper {
    public abstract Book create(CreateBookRequest request);
}

