package libraryapi.readerservice.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.cdimascio.dotenv.Dotenv;
import libraryapi.readerservice.model.Book;
import libraryapi.readerservice.model.Lending;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Repository;

@Repository
public class ReaderRepositoryHTTP {
    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final Dotenv dotenv = Dotenv.load();
    private final int BookServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_PORT1")));
    private final int LendingServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("LENDING_PORT1")));

    public List<Book> getAllBooks() {
        int targetPort = BookServicePort1;
        List<Book> books;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:" + targetPort + "/api/books/internal"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .registerModule(new JavaTimeModule());

                books = objectMapper.readValue(response.body(), new TypeReference<List<Book>>() {});
            } else {
                throw new RuntimeException("Failed to fetch books: " + response.statusCode());
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return books;
    }

    public List<Lending> getAllLendings() {
        int targetPort = LendingServicePort1;
        List<Lending> lendings;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:" + targetPort + "/api/lendings/internal"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .registerModule(new JavaTimeModule());

                lendings = objectMapper.readValue(response.body(), new TypeReference<List<Lending>>() {});
            } else {
                throw new RuntimeException("Failed to fetch lendings: " + response.statusCode());
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return lendings;
    }
}
