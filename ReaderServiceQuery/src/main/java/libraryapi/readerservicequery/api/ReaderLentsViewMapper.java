package libraryapi.readerservicequery.api;

import org.mapstruct.Mapper;
import libraryapi.readerservicequery.model.Reader;

@Mapper(componentModel = "spring")
public abstract class ReaderLentsViewMapper {
    public abstract Iterable<ReaderLentsView> toReaderLentsView (Iterable<Reader> readers);
}
