package libraryapi.bookservicequery.services;

import libraryapi.bookservicequery.model.*;
import libraryapi.bookservicequery.repositories.BookRepository;
import libraryapi.bookservicequery.repositories.LendingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LendingRepository lendingRepository;

    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookService = new BookServiceImpl(bookRepository, null, lendingRepository);
    }


    @Test
    void getBook_ShouldReturnEmpty_WhenBookDoesNotExist() {
        when(bookRepository.findBookByIsbn("978-3-16-148410-0")).thenReturn(Optional.empty());

        Optional<Book> result = bookService.getBook("978-3-16-148410-0");

        assertFalse(result.isPresent());
    }

}
