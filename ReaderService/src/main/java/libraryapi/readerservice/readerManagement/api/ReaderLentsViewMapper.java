package libraryapi.readerservice.readerManagement.api;

import org.mapstruct.Mapper;
import libraryapi.readerservice.readerManagement.model.Reader;

@Mapper(componentModel = "spring")
public abstract class ReaderLentsViewMapper {
    public abstract Iterable<ReaderLentsView> toReaderLentsView (Iterable<Reader> readers);
}
