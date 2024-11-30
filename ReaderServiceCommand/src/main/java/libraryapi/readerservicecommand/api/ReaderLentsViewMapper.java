package libraryapi.readerservicecommand.api;

import org.mapstruct.Mapper;
import libraryapi.readerservicecommand.model.Reader;

@Mapper(componentModel = "spring")
public abstract class ReaderLentsViewMapper {
    public abstract Iterable<ReaderLentsView> toReaderLentsView (Iterable<Reader> readers);
}
