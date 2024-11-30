package libraryapi.bookservicequery.services;

import org.mapstruct.Mapper;
import libraryapi.bookservicequery.model.Book;

@Mapper(componentModel = "spring")
public abstract class EditBookMapper {
    public abstract Book create(CreateBookRequest request);
}

