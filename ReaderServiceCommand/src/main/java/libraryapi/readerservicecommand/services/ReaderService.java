package libraryapi.readerservicecommand.services;

import libraryapi.readerservicecommand.model.Reader;
import org.springframework.web.multipart.MultipartFile;
import libraryapi.readerservicecommand.fileStorage.UploadFileResponse;

public interface ReaderService {
    Reader createReader(final EditReaderRequest resource, MultipartFile photo);
    Reader updateReader(final Long id, final EditReaderRequest resource, final long desiredVersion);
    Reader partialUpdateReader(final Long id, final EditReaderRequest resource, final long desiredVersion);
    UploadFileResponse doUploadFile(final String id, final MultipartFile file);
}
