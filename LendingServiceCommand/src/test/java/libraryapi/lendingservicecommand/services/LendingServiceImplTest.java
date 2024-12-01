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

    @Test
    void createLending() {
        // Arrange
        Long readerId = 1L;
        Long bookId = 1L;

        // Criando um livro e leitor mockado para serem usados no teste
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");

        Reader reader = new Reader();
        reader.setId(readerId);
        reader.setName("Test Reader");

        // Criando o objeto de requisição para o empréstimo
        CreateLendingRequest request = new CreateLendingRequest();
        request.setBookId(bookId);
        request.setReaderId(readerId);

        // Configuração dos mocks
        when(bookRepository.findAll()).thenReturn(List.of(book)); // Mockando o retorno do repositório de livros
        when(readerRepository.findAll()).thenReturn(List.of(reader)); // Mockando o retorno do repositório de leitores
        when(lendingRepository.findOverdueBooksByReaderId(readerId)).thenReturn(new ArrayList<>()); // Nenhum livro vencido
        when(lendingRepository.findAlreadyLendedBook(readerId, bookId)).thenReturn(new ArrayList<>()); // Nenhum livro já emprestado
        when(lendingRepository.countLentBooksByReaderId(readerId)).thenReturn(0); // O leitor ainda não tem livros emprestados

        // Mockando o retorno do save do lendingRepository
        Lending lending = new Lending();
        lending.setId(1L); // Definindo um ID para o empréstimo
        lending.setLendingCode("2024/1"); // Definindo o código de empréstimo
        when(lendingRepository.save(any(Lending.class))).thenReturn(lending); // Mockando o retorno do save

        // Act
        Lending result = lendingService.createLending(request); // Executando o método

        // Assert
        assertNotNull(result); // Verifica se o empréstimo retornado não é nulo
        assertEquals(bookId, result.getBookId()); // Verifica se o ID do livro está correto
        assertEquals(readerId, result.getReaderId()); // Verifica se o ID do leitor está correto
        assertEquals("2024/1", result.getLendingCode()); // Verifica se o código do empréstimo está correto
        verify(lendingRepository, times(1)).save(result); // Verifica se o save foi chamado uma vez
        verify(sender, times(1)).sendSyncLending(result); // Verifica se o envio do empréstimo foi feito
    }


    @Test
    void createLending_BookNotFound() {
        // Arrange
        CreateLendingRequest request = new CreateLendingRequest();
        request.setBookId(999L); // Non-existent book ID

        when(bookRepository.findAll()).thenReturn(new ArrayList<>());

        // Act and Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> lendingService.createLending(request));
        assertEquals("[ERROR] Book not found with ID: 999", thrown.getMessage());
    }

    @Test
    void returnBook() {
        // Arrange
        Long lendingId = 1L;
        String lendingCode = "2024/1";

        Lending existingLending = new Lending();
        existingLending.setId(lendingId);
        existingLending.setLendingCode(lendingCode);
        existingLending.setReturned(false);
        existingLending.setLimitDate(LocalDate.now().minusDays(5));  // 5 days overdue

        EditLendingRequest returnRequest = new EditLendingRequest();
        returnRequest.setLendingCode(lendingCode);
        returnRequest.setComment("Returned late");

        when(lendingRepository.findByLendingCode(lendingCode)).thenReturn(Optional.of(existingLending));
        when(lendingRepository.save(any(Lending.class))).thenReturn(existingLending);

        // Act
        Lending returnedLending = lendingService.returnBook(returnRequest);

        // Assert
        assertTrue(returnedLending.isReturned());
        assertNotNull(returnedLending.getReturnedDate());
        assertTrue(returnedLending.getDaysOverdue() > 0);  // Days overdue should be greater than 0
        assertEquals("Returned late", returnedLending.getComment());
        verify(lendingRepository, times(1)).save(returnedLending);
        verify(sender, times(1)).sendSyncLending(returnedLending);
    }

    @Test
    void returnBook_LendingNotFound() {
        // Arrange
        String invalidLendingCode = "2024/999"; // Non-existent lending code
        EditLendingRequest returnRequest = new EditLendingRequest();
        returnRequest.setLendingCode(invalidLendingCode);

        when(lendingRepository.findByLendingCode(invalidLendingCode)).thenReturn(Optional.empty());

        // Act and Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> lendingService.returnBook(returnRequest));
        assertEquals("[ERROR] Lending not found", thrown.getMessage());
    }

    @Test
    void manageInternalLending() {
        // Arrange
        Long lendingId = 1L;
        Lending lending = new Lending();
        lending.setId(lendingId);
        when(lendingRepository.save(lending)).thenReturn(lending);

        // Act
        Lending result = lendingService.manageInternalLending(lending);

        // Assert
        assertEquals(lendingId, result.getId());
        verify(lendingRepository, times(1)).save(lending);
    }
}
