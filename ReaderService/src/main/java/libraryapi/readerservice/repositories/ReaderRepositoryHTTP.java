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

import libraryapi.readerservice.model.Reader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

@Repository
public class ReaderRepositoryHTTP {
    @Autowired
    private Environment env;

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final Dotenv dotenv = Dotenv.load();
    private final int BookServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_PORT1")));
    private final int BookServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_PORT2")));
    private final int LendingServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("LENDING_PORT1")));
    private final int LendingServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("LENDING_PORT2")));
    private final int ReaderServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("READER_PORT1")));
    private final int ReaderServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("READER_PORT2")));

    public List<Book> getAllBooks() {
        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        int targetPort = (currentPort == ReaderServicePort1) ? BookServicePort1 : BookServicePort2;
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
        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        int targetPort = (currentPort == ReaderServicePort1) ? LendingServicePort1 : LendingServicePort2;
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

    public void manageInternalReader(Reader reader) {
        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        int targetPort = (currentPort == ReaderServicePort1) ? ReaderServicePort2 :ReaderServicePort1;

        try {
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .registerModule(new JavaTimeModule());

            String readerJson = objectMapper.writeValueAsString(reader);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:" + targetPort + "/api/readers/internal"))
                    .PUT(HttpRequest.BodyPublishers.ofString(readerJson))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
