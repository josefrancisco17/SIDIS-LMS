package libraryapi.bookservice.services;

import org.mapstruct.Mapper;
import libraryapi.bookservice.model.Book;

@Mapper(componentModel = "spring")
public abstract class EditBookMapper {
    public abstract Book create(CreateBookRequest request);
}

