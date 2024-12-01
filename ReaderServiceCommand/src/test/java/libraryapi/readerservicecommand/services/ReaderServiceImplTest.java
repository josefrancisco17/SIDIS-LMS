package libraryapi.readerservicecommand.services;

import libraryapi.readerservicecommand.model.Reader;
import libraryapi.readerservicecommand.model.ReaderPhoto;
import libraryapi.readerservicecommand.repositories.ReaderRepository;
import libraryapi.readerservicecommand.repositories.ReaderPhotoRepository;
import libraryapi.readerservicecommand.fileStorage.FileStorageService;
import libraryapi.readerservicecommand.fileStorage.UploadFileResponse;  // Importação da classe UploadFileResponse
import libraryapi.readerservicecommand.rabbitMQ.producer.Sender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ReaderServiceImplTest {

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private ReaderPhotoRepository readerPhotoRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private Sender sender;

    @InjectMocks
    private ReaderServiceImpl readerService;

    private Reader reader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Criação de um reader de teste
        reader = new Reader();
        reader.setId(1L);
        reader.setReaderCode("2024/1");
        reader.setName("Test User");
        reader.setEmail("test@example.com");
        reader.setDateOfBirth(LocalDate.of(2000, 1, 1));
        reader.setPhoneNumber(123456789);
        reader.setGDBRConsent(true);
    }

    @Test
    void createReader() throws IOException {
        // Criando um Mock MultipartFile para o teste de upload de foto
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getBytes()).thenReturn(new byte[0]);
        when(mockFile.getContentType()).thenReturn("image/jpeg");

        // Definindo o comportamento do repositório para o mock
        when(readerRepository.findMaxReaderId()).thenReturn(0);
        when(readerRepository.save(any(Reader.class))).thenReturn(reader);

        // Chamando o método
        EditReaderRequest request = new EditReaderRequest(
                "Test User", "test@example.com", LocalDate.of(2000, 1, 1), 123456789, true, null);
        Reader createdReader = readerService.createReader(request, mockFile);

        // Verificação dos resultados
        assertNotNull(createdReader);
        assertEquals("test@example.com", createdReader.getEmail());
        assertEquals("Test User", createdReader.getName());

        // Verificando interações
        verify(readerRepository, times(1)).save(any(Reader.class));
        verify(fileStorageService, times(1)).storeFile(anyString(), eq(mockFile));
    }

    @Test
    void updateReader() {
        // Atualizando um reader mockado
        when(readerRepository.findById(1L)).thenReturn(Optional.of(reader));
        when(readerRepository.save(any(Reader.class))).thenReturn(reader);

        EditReaderRequest updatedRequest = new EditReaderRequest(
                "Updated User", "updated@example.com", LocalDate.of(1995, 5, 10), 987654321, true, null);

        Reader updatedReader = readerService.updateReader(1L, updatedRequest, 1L);

        assertNotNull(updatedReader);
        assertEquals("Updated User", updatedReader.getName());
        assertEquals("updated@example.com", updatedReader.getEmail());
    }

    @Test
    void partialUpdateReader() {
        // Mockando o comportamento para partial update
        when(readerRepository.findById(1L)).thenReturn(Optional.of(reader));
        when(readerRepository.save(any(Reader.class))).thenReturn(reader);

        EditReaderRequest partialRequest = new EditReaderRequest(
                "Partial Update User", "partial@example.com", LocalDate.of(1995, 5, 10), 987654321, true, null);

        Reader partialUpdatedReader = readerService.partialUpdateReader(1L, partialRequest, 1L);

        assertNotNull(partialUpdatedReader);
        assertEquals("Partial Update User", partialUpdatedReader.getName());
        assertEquals("partial@example.com", partialUpdatedReader.getEmail());
    }

    @Test
    void doUploadFile() throws IOException {
        // Criando um mock de MultipartFile
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getBytes()).thenReturn(new byte[10]);
        when(mockFile.getContentType()).thenReturn("image/png");

        // Mockando o repositório de fotos
        ReaderPhoto mockPhoto = new ReaderPhoto();
        when(readerPhotoRepository.save(any(ReaderPhoto.class))).thenReturn(mockPhoto);

        // Simulando o comportamento do método de upload
        String fileName = "test-file.png";
        String fileDownloadUri = "/files/test-file.png";
        UploadFileResponse mockResponse = new UploadFileResponse(fileName, fileDownloadUri, "image/png", 10);

        // Corrigindo a simulação para retornar um UploadFileResponse, e não String
        when(fileStorageService.storeFile(anyString(), eq(mockFile))).thenReturn(mockResponse);

        // Chamando o método
        UploadFileResponse response = readerService.doUploadFile("1", mockFile);

        // Verificando as interações
        assertNotNull(response);
        assertEquals("image/png", response.getFileType()); // Utilizando getFileType() conforme a classe UploadFileResponse
        assertTrue(response.getSize() > 0); // Verificando o tamanho do arquivo
        verify(readerPhotoRepository, times(1)).save(any(ReaderPhoto.class));
    }

}
