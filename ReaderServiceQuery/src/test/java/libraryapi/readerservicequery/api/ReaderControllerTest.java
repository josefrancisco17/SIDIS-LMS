package libraryapi.readerservicequery.api;

import libraryapi.readerservicequery.model.Reader;
import libraryapi.readerservicequery.model.ReaderPhoto;
import libraryapi.readerservicequery.services.ReaderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReaderController.class)
class ReaderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReaderServiceImpl readerService;

    @MockBean
    private ReaderViewMapper readerViewMapper;

    @MockBean
    private ReaderProfileViewMapper readerProfileViewMapper;

    @MockBean
    private BookViewMapper bookViewMapper;

    private Reader mockReader;
    private ReaderPhoto mockReaderPhoto;

    @BeforeEach
    void setUp() {
        mockReader = new Reader();
        mockReader.setId(1L);
        mockReader.setReaderCode("readerCode123");
        mockReader.setName("John Doe");
        mockReader.setEmail("john@example.com");
        mockReader.setPhoneNumber(123456789);

        mockReaderPhoto = new ReaderPhoto();
        mockReaderPhoto.setImage(new byte[]{1, 2, 3});
        mockReaderPhoto.setContentType("image/png");
    }

    @Test
    void getReader() throws Exception {
        when(readerService.getReader(1L, null)).thenReturn(Optional.of(mockReader));
        when(readerProfileViewMapper.toReaderProfileView(mockReader)).thenReturn(new ReaderProfileView());

        mockMvc.perform(get("/api/readers/{readerId}", 1L)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(readerService, times(1)).getReader(1L, null);
    }


    @Test
    void getAllReaders() throws Exception {
        when(readerService.getAllReaders()).thenReturn(Collections.singletonList(mockReader));

        mockMvc.perform(get("/api/readers/internal"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(readerService, times(1)).getAllReaders();
    }

    @Test
    void getTopReaders() throws Exception {
        when(readerService.getTopReaders()).thenReturn(Collections.singletonList(mockReader));
        when(readerViewMapper.toReaderView(mockReader)).thenReturn(new ReaderView());

        mockMvc.perform(get("/api/readers/top-readers")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(readerService, times(1)).getTopReaders();
    }


    @Test
    void getReaderPhoto() throws Exception {
        when(readerService.getReaderPhoto("1")).thenReturn(mockReaderPhoto);

        mockMvc.perform(get("/api/readers/{readerId}/photo", "1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"photo.png\""));

        verify(readerService, times(1)).getReaderPhoto("1");
    }
}
