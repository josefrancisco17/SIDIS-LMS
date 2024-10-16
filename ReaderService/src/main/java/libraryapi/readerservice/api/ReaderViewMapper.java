package libraryapi.readerservice.api;

import org.mapstruct.Mapper;
import libraryapi.readerservice.model.Reader;

@Mapper(componentModel = "spring")
public abstract class ReaderViewMapper {
    public abstract ReaderView toReaderView(Reader reader);
    public abstract Iterable<ReaderView> toReaderView(Iterable<Reader> readers);
}
