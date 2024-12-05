package libraryapi.readerservicecommand.api;

import libraryapi.readerservicecommand.services.ReaderServiceImpl;
import libraryapi.readerservicecommand.services.EditReaderRequest;
import libraryapi.readerservicecommand.model.Reader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReaderControllerTest {

    @Mock
    private ReaderServiceImpl readerService;

    @Mock
    private ReaderViewMapper readerViewMapper;

    @InjectMocks
    private ReaderController readerController;

    @Autowired
    private MockMvc mockMvc;

    private Reader mockReader;
    private EditReaderRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ReaderController(
                mock(ReaderServiceImpl.class),
                mock(ReaderViewMapper.class))
        ).build();

        mockReader = new Reader();
        mockReader.setId(1L);
        mockReader.setReaderCode("readerCode123");
        mockReader.setName("John Doe");
        mockReader.setEmail("john@example.com");
        mockReader.setDateOfBirth(LocalDate.of(1990, 1, 1));
        mockReader.setAge(34);
        mockReader.setPhoneNumber(123456789);
        mockReader.setGDBRConsent(true);
        mockReader.setInterests(List.of("Books", "Technology"));
        mockReader.setVersion(1L);

        mockRequest = new EditReaderRequest();
        mockRequest.setName("John Doe");
        mockRequest.setEmail("john@example.com");
    }

    @Test
    void createReader() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("photo", "photo.jpg", "image/jpeg", new byte[0]);
        when(readerService.createReader(any(), any())).thenReturn(mockReader);
        when(readerViewMapper.toReaderView(mockReader)).thenReturn(new ReaderView());

        // Act & Assert
        mockMvc.perform(multipart("/api/readers")
                        .file(file)
                        .param("reader", "{\"name\":\"John Doe\",\"email\":\"john@example.com\"}")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/readers/1"))
                .andExpect(header().string("ETag", "1"));
    }

    @Test
    void updateReader() throws Exception {
        // Arrange
        String ifMatchHeader = "\"1\"";
        when(readerService.updateReader(eq(1L), any(), eq(1L))).thenReturn(mockReader);
        when(readerViewMapper.toReaderView(mockReader)).thenReturn(new ReaderView());

        // Act & Assert
        mockMvc.perform(put("/api/readers/{readerId}", 1)
                        .header("If-Match", ifMatchHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John Updated\",\"email\":\"john_updated@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "1"));
    }

    @Test
    void partialUpdateReader() throws Exception {
        // Arrange
        String ifMatchHeader = "\"1\"";
        when(readerService.partialUpdateReader(eq(1L), any(), eq(1L))).thenReturn(mockReader);
        when(readerViewMapper.toReaderView(mockReader)).thenReturn(new ReaderView());

        // Act & Assert
        mockMvc.perform(patch("/api/readers/{readerId}", 1)
                        .header("If-Match", ifMatchHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"john_partial@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "1"));
    }
}
