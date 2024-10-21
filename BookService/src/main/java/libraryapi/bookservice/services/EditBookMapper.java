package libraryapi.bookservice.services;

import libraryapi.bookservice.model.Author;
import org.mapstruct.Mapper;
import libraryapi.bookservice.model.Book;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class EditBookMapper {
    public abstract Book create(CreateBookRequest request);
}

