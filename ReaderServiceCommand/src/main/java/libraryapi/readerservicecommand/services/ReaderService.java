package libraryapi.readerservicecommand.services;

import jakarta.servlet.http.HttpServletRequest;
import libraryapi.readerservicecommand.model.Book;
import libraryapi.readerservicecommand.model.Reader;
import libraryapi.readerservicecommand.model.ReaderPhoto;
import libraryapi.readerservicecommand.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import libraryapi.readerservicecommand.fileStorage.UploadFileResponse;

import java.util.Optional;

public interface ReaderService {
    Reader createReader(final EditReaderRequest resource, MultipartFile photo);
    Reader updateReader(final Long id, final EditReaderRequest resource, final long desiredVersion);
    Reader partialUpdateReader(final Long id, final EditReaderRequest resource, final long desiredVersion);
    UploadFileResponse doUploadFile(final String id, final MultipartFile file);
}
