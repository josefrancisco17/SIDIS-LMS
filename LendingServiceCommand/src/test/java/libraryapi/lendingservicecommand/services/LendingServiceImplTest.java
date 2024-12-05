package libraryapi.lendingservicecommand.services;

import libraryapi.lendingservicecommand.model.Book;
import libraryapi.lendingservicecommand.model.Lending;
import libraryapi.lendingservicecommand.model.Reader;
import libraryapi.lendingservicecommand.rabbitMQ.producer.Sender;
import libraryapi.lendingservicecommand.repositories.BookRepository;
import libraryapi.lendingservicecommand.repositories.LendingRepository;
import libraryapi.lendingservicecommand.repositories.ReaderRepository;
import libraryapi.lendingservicecommand.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LendingServiceImplTest {

    @Mock
    private LendingRepository lendingRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private Sender sender;

    @InjectMocks
    private LendingServiceImpl lendingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /*@Test
    void createLending() {
        Long readerId = 1L;
        Long bookId = 1L;

        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");

        Reader reader = new Reader();
        reader.setId(readerId);
        reader.setName("Test Reader");

        CreateLendingRequest request = new CreateLendingRequest();
        request.setBookId(bookId);
        request.setReaderId(readerId);

        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(readerRepository.findAll()).thenReturn(List.of(reader));
        when(lendingRepository.findOverdueBooksByReaderId(readerId)).thenReturn(new ArrayList<>());
        when(lendingRepository.findAlreadyLendedBook(readerId, bookId)).thenReturn(new ArrayList<>());
        when(lendingRepository.countLentBooksByReaderId(readerId)).thenReturn(0);

        Lending lending = new Lending();
        lending.setId(1L);
        lending.setLendingCode("2024/1");
        when(lendingRepository.save(any(Lending.class))).thenReturn(lending);

        Lending result = lendingService.createLending(request);

        assertNotNull(result);
        assertEquals(bookId, result.getBookId());
        assertEquals(readerId, result.getReaderId());
        assertEquals("2024/1", result.getLendingCode());
        verify(lendingRepository, times(1)).save(result);
        verify(sender, times(1)).sendSyncLending(result);
    }*/


    @Test
    void createLending_BookNotFound() {
        // Arrange
        CreateLendingRequest request = new CreateLendingRequest();
        request.setBookId(999L);

        when(bookRepository.findAll()).thenReturn(new ArrayList<>());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> lendingService.createLending(request));
        assertEquals("[ERROR] Book not found with ID: 999", thrown.getMessage());
    }

    @Test
    void returnBook() {
        Long lendingId = 1L;
        String lendingCode = "2024/1";

        Lending existingLending = new Lending();
        existingLending.setId(lendingId);
        existingLending.setLendingCode(lendingCode);
        existingLending.setReturned(false);
        existingLending.setLimitDate(LocalDate.now().minusDays(5));

        EditLendingRequest returnRequest = new EditLendingRequest();
        returnRequest.setLendingCode(lendingCode);
        returnRequest.setComment("Returned late");

        when(lendingRepository.findByLendingCode(lendingCode)).thenReturn(Optional.of(existingLending));
        when(lendingRepository.save(any(Lending.class))).thenReturn(existingLending);

        Lending returnedLending = lendingService.returnBook(returnRequest);

        assertTrue(returnedLending.isReturned());
        assertNotNull(returnedLending.getReturnedDate());
        assertTrue(returnedLending.getDaysOverdue() > 0);  // Days overdue should be greater than 0
        assertEquals("Returned late", returnedLending.getComment());
        verify(lendingRepository, times(1)).save(returnedLending);
        verify(sender, times(1)).sendSyncLending(returnedLending);
    }

    @Test
    void returnBook_LendingNotFound() {
        String invalidLendingCode = "2024/999";
        EditLendingRequest returnRequest = new EditLendingRequest();
        returnRequest.setLendingCode(invalidLendingCode);

        when(lendingRepository.findByLendingCode(invalidLendingCode)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> lendingService.returnBook(returnRequest));
        assertEquals("[ERROR] Lending not found", thrown.getMessage());
    }

    @Test
    void manageInternalLending() {
        Long lendingId = 1L;
        Lending lending = new Lending();
        lending.setId(lendingId);
        when(lendingRepository.save(lending)).thenReturn(lending);

        Lending result = lendingService.manageInternalLending(lending);

        assertEquals(lendingId, result.getId());
        verify(lendingRepository, times(1)).save(lending);
    }
}
