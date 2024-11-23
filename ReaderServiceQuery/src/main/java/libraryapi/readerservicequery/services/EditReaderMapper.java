package libraryapi.readerservicequery.services;

import org.mapstruct.Mapper;
import libraryapi.readerservicequery.model.Reader;

@Mapper(componentModel = "spring")
public abstract class EditReaderMapper {
    public abstract Reader create(EditReaderRequest request);
}
