package libraryapi.readerservice.services;

import org.mapstruct.Mapper;
import libraryapi.readerservice.model.Reader;

@Mapper(componentModel = "spring")
public abstract class EditReaderMapper {
    public abstract Reader create(EditReaderRequest request);
}
