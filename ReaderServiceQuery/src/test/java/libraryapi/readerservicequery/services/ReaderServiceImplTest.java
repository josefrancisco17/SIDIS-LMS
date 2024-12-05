package libraryapi.readerservicequery.services;

import jakarta.servlet.http.HttpServletRequest;
import libraryapi.readerservicequery.model.*;
import libraryapi.readerservicequery.repositories.ReaderRepository;
import libraryapi.readerservicequery.repositories.LendingRepository;
import libraryapi.readerservicequery.repositories.BookRepository;
import libraryapi.readerservicequery.util.BookUtil;
import libraryapi.readerservicequery.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import java.time.LocalDate;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ReaderServiceImplTest {

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private LendingRepository lendingRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ReaderServiceImpl readerService;

    private Reader reader;
    private Lending lending;
    private Book book;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup common test data
        reader = new Reader();
        reader.setId(1L);
        reader.setName("Test Reader");
        reader.setEmail("reader@example.com");
        reader.setPhoneNumber(123456789);
        reader.setDateOfBirth(LocalDate.of(1990, 1, 1));

        lending = new Lending();
        lending.setReaderId(reader.getId());
        lending.setBookId(1L);

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setGenre(new Genre("Fiction"));
    }

    @Test
    void getReadersByName() {
        List<Reader> readers = Collections.singletonList(reader);
        Pageable pageable = PageRequest.of(0, 10);
        when(readerRepository.findAll()).thenReturn(readers);

        Page<Reader> result = readerService.getReadersByName("Test", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Test Reader", result.getContent().get(0).getName());
    }

    @Test
    void getReaders() {
        List<Reader> readers = Collections.singletonList(reader);
        Pageable pageable = PageRequest.of(0, 10);
        when(readerRepository.findAll(pageable)).thenReturn(new PageImpl<>(readers));

        Page<Reader> result = readerService.getReaders(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Test Reader", result.getContent().get(0).getName());
    }

    @Test
    void getAllReaders() {
        List<Reader> readers = Collections.singletonList(reader);
        when(readerRepository.findAll()).thenReturn(readers);

        Iterable<Reader> result = readerService.getAllReaders();

        assertNotNull(result);
        assertEquals(1, ((List<Reader>) result).size());
    }

    @Test
    void getTopReaders() {
        when(lendingRepository.findAll()).thenReturn(Collections.singletonList(lending));
        when(readerRepository.findById(reader.getId())).thenReturn(Optional.of(reader));

        Iterable<Reader> result = readerService.getTopReaders();

        assertNotNull(result);
        assertTrue(((List<Reader>) result).size() > 0);
    }

    /*@Test
    void getReader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt-token");
        when(readerRepository.findById(1L)).thenReturn(Optional.of(reader));

        Optional<Reader> result = readerService.getReader(1L, request);

        assertTrue(result.isPresent());
        assertEquals("Test Reader", result.get().getName());
    }*/

   /* @Test
    void getSuggestedBooks() {
        List<Book> books = Collections.singletonList(book);
        Pageable pageable = PageRequest.of(0, 10);
        when(readerRepository.findById(reader.getId())).thenReturn(Optional.of(reader));
        when(bookRepository.findAll()).thenReturn(books);

        Page<Book> result = readerService.getSuggestedBooks(reader.getId(), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Book", result.getContent().get(0).getTitle());
    }*/

    @Test
    void getReadersByPhoneNumberAndEmail() {
        List<Reader> readers = Collections.singletonList(reader);
        Pageable pageable = PageRequest.of(0, 10);
        when(readerRepository.findAll()).thenReturn(readers);

        Page<Reader> result = readerService.getReadersByPhoneNumberAndEmail("123456789", "reader@example.com", pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getReadersByPhoneNumber() {
        List<Reader> readers = Collections.singletonList(reader);
        Pageable pageable = PageRequest.of(0, 10);
        when(readerRepository.findAll()).thenReturn(readers);

        Page<Reader> result = readerService.getReadersByPhoneNumber("123456789", pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getReadersByEmail() {
        List<Reader> readers = Collections.singletonList(reader);
        Pageable pageable = PageRequest.of(0, 10);
        when(readerRepository.findAll()).thenReturn(readers);

        Page<Reader> result = readerService.getReadersByEmail("reader@example.com", pageable);

        assertEquals(1, result.getTotalElements());
    }

}
