package libraryapi.readerservice.readerManagement.services;

import org.mapstruct.Mapper;
import libraryapi.readerservice.readerManagement.model.Reader;

@Mapper(componentModel = "spring")
public abstract class EditReaderMapper {
    public abstract Reader create(EditReaderRequest request);
}
