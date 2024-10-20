package libraryapi.bookservice.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.cdimascio.dotenv.Dotenv;
import libraryapi.bookservice.model.Book;
import libraryapi.bookservice.model.BookAuthor;
import libraryapi.bookservice.model.Lending;
import org.springframework.beans.factory.annotation.Value;
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

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

@Repository
public class BookRepositoryHTTP {
    @Autowired
    private Environment env;

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final Dotenv dotenv = Dotenv.load();
    private final int BookServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_PORT1")));
    private final int BookServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_PORT2")));
    private final int LendingServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("LENDING_PORT1")));
    private final int LendingServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("LENDING_PORT2")));

    public List<Lending> getAllLendings() {
        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        int targetPort = (currentPort == BookServicePort1) ? LendingServicePort1 : LendingServicePort2;
        List<Lending> lendings = new ArrayList<>();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:" + targetPort + "/api/lendings/internal"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .registerModule(new JavaTimeModule());

                // Deserialize the JSON response
                lendings = objectMapper.readValue(response.body(), new TypeReference<List<Lending>>() {});
            }

        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        return lendings;
    }

    public void manageInternalBook(Book book) {
        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        int targetPort = (currentPort == BookServicePort1) ? BookServicePort2 :BookServicePort1;

        try {
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .registerModule(new JavaTimeModule());

            String bookJson = objectMapper.writeValueAsString(book);

            System.out.println(bookJson);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:" + targetPort + "/api/books/internal"))
                    .PUT(HttpRequest.BodyPublishers.ofString(bookJson))
                    .header("Content-Type", "application/json")
                    .build();
            System.out.println(request.method());
            System.out.println(request.bodyPublisher());
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void manageInternalBookAuthors(List<BookAuthor> bookAuthorList) {
        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        int targetPort = (currentPort == BookServicePort1) ? BookServicePort2 :BookServicePort1;
        try {
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .registerModule(new JavaTimeModule());

            String bookAuthorsJson = objectMapper.writeValueAsString(bookAuthorList);
            System.out.println(bookAuthorsJson);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:" + targetPort + "/api/books/internal/book-authors"))
                    .PUT(HttpRequest.BodyPublishers.ofString(bookAuthorsJson))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}

