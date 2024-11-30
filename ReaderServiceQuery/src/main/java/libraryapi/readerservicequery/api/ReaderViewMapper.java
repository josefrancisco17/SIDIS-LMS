package libraryapi.readerservicequery.api;

import org.mapstruct.Mapper;
import libraryapi.readerservicequery.model.Reader;

@Mapper(componentModel = "spring")
public abstract class ReaderViewMapper {
    public abstract ReaderView toReaderView(Reader reader);
    public abstract Iterable<ReaderView> toReaderView(Iterable<Reader> readers);
}
