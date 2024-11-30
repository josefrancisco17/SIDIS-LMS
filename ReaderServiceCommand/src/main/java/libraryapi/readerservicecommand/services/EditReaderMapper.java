package libraryapi.readerservicecommand.services;

import org.mapstruct.Mapper;
import libraryapi.readerservicecommand.model.Reader;

@Mapper(componentModel = "spring")
public abstract class EditReaderMapper {
    public abstract Reader create(EditReaderRequest request);
}
