package libraryapi.lendingservice.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.cdimascio.dotenv.Dotenv;
import libraryapi.lendingservice.model.Book;
import libraryapi.lendingservice.model.Genre;
import libraryapi.lendingservice.model.Lending;
import libraryapi.lendingservice.model.Reader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class LendingRepositoryHTTP {
    @Autowired
    private Environment env;

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final Dotenv dotenv = Dotenv.load();
    private final int BookServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_PORT1")));
    private final int BookServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_PORT2")));
    private final int ReaderServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("READER_PORT1")));
    private final int ReaderServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("READER_PORT2")));
    private final int LendingServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("LENDING_PORT1")));
    private final int LendingServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("LENDING_PORT2")));

    public List<Book> getAllBooks() {
        int targetPort;
        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        try {
            targetPort = (currentPort == LendingServicePort1) ? BookServicePort1 : BookServicePort2;
        } catch (NumberFormatException | NullPointerException e) {
            throw new RuntimeException("Invalid or missing server port: " + e.getMessage(), e);
        }

        List<Book> books = new ArrayList<>();

        try {
            String url = "http://localhost:" + targetPort + "/api/books/internal";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();


            System.out.println("Request URL: " + url);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Body: " + response.body());

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

    public List<Reader> getAllReaders() {
        int targetPort;
        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        try {
            targetPort = (currentPort == LendingServicePort1) ? ReaderServicePort1 :ReaderServicePort2;
        } catch (NumberFormatException | NullPointerException e) {
            throw new RuntimeException("Invalid or missing server port: " + e.getMessage(), e);
        }

        List<Reader> readers = new ArrayList<>();

        try {
            String url = "http://localhost:" + targetPort + "/api/readers/internal";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            System.out.println("Request URL: " + url);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Body: " + response.body());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .registerModule(new JavaTimeModule());

                // Deserialize the JSON response
                readers = objectMapper.readValue(response.body(), new TypeReference<List<Reader>>() {});
            }

        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        return readers;
    }

    public List<Genre> getAllGenres() {
        int targetPort;
        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        try {
            targetPort = (currentPort == LendingServicePort1) ? BookServicePort1 : BookServicePort2;
        } catch (NumberFormatException | NullPointerException e) {
            throw new RuntimeException("Invalid or missing server port: " + e.getMessage(), e);
        }

        List<Genre> genres = new ArrayList<>();

        try {
            String url = "http://localhost:" + targetPort + "/api/books/internal/genres";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            System.out.println("Request URL: " + url);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Body: " + response.body());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .registerModule(new JavaTimeModule());

                // Deserialize the JSON response
                genres = objectMapper.readValue(response.body(), new TypeReference<List<Genre>>() {});
            }

        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        return genres;
    }

    public void createInternalLending(Lending lending) {
        int targetPort;
        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        try {
            targetPort = (currentPort == LendingServicePort1) ? LendingServicePort2 :LendingServicePort1;
        } catch (NumberFormatException | NullPointerException e) {
            throw new RuntimeException("Invalid or missing server port: " + e.getMessage(), e);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .registerModule(new JavaTimeModule());

            String lendingJson = objectMapper.writeValueAsString(lending);

            String url = "http://localhost:" + targetPort + "/api/lendings/internal";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .POST(HttpRequest.BodyPublishers.ofString(lendingJson))
                    .header("Content-Type", "application/json")
                    .build();

            System.out.println("Request Body: " + lendingJson);
            System.out.println("Request URL: " + url);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Body: " + response.body());

        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void returnInternalBook(Lending lending) {
        int targetPort;
        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        try {
            targetPort = (currentPort == LendingServicePort1) ? LendingServicePort2 :LendingServicePort1;
        } catch (NumberFormatException | NullPointerException e) {
            throw new RuntimeException("Invalid or missing server port: " + e.getMessage(), e);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .registerModule(new JavaTimeModule());

            String lendingJson = objectMapper.writeValueAsString(lending);

            String url = "http://localhost:" + targetPort + "/api/lendings/internal/return";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .POST(HttpRequest.BodyPublishers.ofString(lendingJson))
                    .header("Content-Type", "application/json")
                    .build();

            System.out.println("Request Body: " + lendingJson);
            System.out.println("Request URL: " + url);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Body: " + response.body());

        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
