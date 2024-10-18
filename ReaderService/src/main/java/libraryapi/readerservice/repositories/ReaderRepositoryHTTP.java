package libraryapi.readerservice.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.cdimascio.dotenv.Dotenv;
import libraryapi.readerservice.model.Book;
import libraryapi.readerservice.model.Genre;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ReaderRepositoryHTTP {
    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final Dotenv dotenv = Dotenv.load();
    private final int BookServicePort1 = Integer.parseInt(dotenv.get("BOOK_PORT1"));

    public List<Book> getAllBooks() {
        int targetPort = BookServicePort1;
        List<Book> books = new ArrayList<>();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:" + targetPort + "/api/books/all"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .registerModule(new JavaTimeModule());

                // Deserialize the JSON response
                books = objectMapper.readValue(response.body(), new TypeReference<List<Book>>() {});
            }

        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        return books;
    }
}
