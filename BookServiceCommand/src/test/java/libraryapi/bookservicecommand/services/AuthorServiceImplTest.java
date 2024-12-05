package libraryapi.bookservicecommand.services;

import libraryapi.bookservicecommand.exceptions.NotFoundException;
import libraryapi.bookservicecommand.fileStorage.FileStorageService;
import libraryapi.bookservicecommand.fileStorage.UploadFileResponse;
import libraryapi.bookservicecommand.model.*;
import libraryapi.bookservicecommand.rabbitMQ.producer.Sender;
import libraryapi.bookservicecommand.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private EditAuthorMapper editAuthorMapper;

    @Mock
    private AuthorPhotoRepository authorPhotoRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private Sender sender;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /*
    @Test
    void createAuthor_Success() {

        EditAuthorRequest request = new EditAuthorRequest();
        request.setName("John Doe");
        request.setShortBio("An experienced author.");

        Author author = new Author();
        author.setId(1L);
        author.setName("John Doe");

        when(editAuthorMapper.create(request)).thenReturn(author);
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        Author createdAuthor = authorService.createAuthor(request, null);

        assertNotNull(createdAuthor);
        assertEquals("John Doe", createdAuthor.getName());
        verify(authorRepository, times(2)).save(any(Author.class));
        verify(sender).sendSyncAuthor(any(Author.class));
    }*/

    @Test
    void manageInternalAuthor_Success() {

        Author author = new Author();
        author.setName("Jane Doe");

        when(authorRepository.save(author)).thenReturn(author);

        Author result = authorService.manageInternalAuthor(author);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        verify(authorRepository).save(author);
    }

    /*
    @Test
    void updateAuthor_Success() {

        Long authorId = 1L;
        EditAuthorRequest request = new EditAuthorRequest();
        request.setName("Updated Name");
        request.setShortBio("Updated Bio");

        Author existingAuthor = new Author();
        existingAuthor.setId(authorId);
        existingAuthor.setName("Old Name");

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.save(existingAuthor)).thenReturn(existingAuthor);

        Author updatedAuthor = authorService.updateAuthor(authorId, request, 1L);

        assertNotNull(updatedAuthor);
        assertEquals("Updated Name", updatedAuthor.getName());
        verify(authorRepository).save(existingAuthor);
        verify(sender).sendSyncAuthor(any(Author.class));
    }*/

    @Test
    void updateAuthor_NotFound_ThrowsException() {

        Long authorId = 1L;
        EditAuthorRequest request = new EditAuthorRequest();

        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            authorService.updateAuthor(authorId, request, 1L);
        });
        verify(authorRepository, never()).save(any());
    }

    /*
    @Test
    void partialUpdateAuthor_Success() {

        Long authorId = 1L;
        EditAuthorRequest request = new EditAuthorRequest();
        request.setName("Partially Updated Name");

        Author existingAuthor = new Author();
        existingAuthor.setId(authorId);
        existingAuthor.setName("Old Name");

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.save(existingAuthor)).thenReturn(existingAuthor);

        Author updatedAuthor = authorService.partialUpdateAuthor(authorId, request, 1L);

        assertNotNull(updatedAuthor);
        assertEquals("Partially Updated Name", updatedAuthor.getName());
        verify(authorRepository).save(existingAuthor);
        verify(sender).sendSyncAuthor(any(Author.class));
    }*/



    @Test
    void validateCreateAuthorRequest_InvalidRequest_ThrowsException() {

        EditAuthorRequest request = new EditAuthorRequest();
        request.setName("");
        request.setShortBio("");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authorService.validateCreateAuthorRequest(request);
        });

        assertEquals("[ERROR] Name and shortBio are mandatory.", exception.getMessage());
    }
}
