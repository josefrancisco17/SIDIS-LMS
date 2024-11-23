package libraryapi.readerservicecommand.api;

import org.mapstruct.Mapper;
import libraryapi.readerservicecommand.model.Reader;

@Mapper(componentModel = "spring")
public abstract class ReaderViewMapper {
    public abstract ReaderView toReaderView(Reader reader);
    public abstract Iterable<ReaderView> toReaderView(Iterable<Reader> readers);
}
