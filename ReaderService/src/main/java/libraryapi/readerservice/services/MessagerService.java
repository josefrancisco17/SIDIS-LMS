package libraryapi.readerservice.services;

import libraryapi.readerservice.model.Genre;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class MessagerService {
    private final HttpClient httpClient;

    public MessagerService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public Optional<Genre> getGenreById(final Long id) {
        String url = "http://localhost:9010/books/genres/" + id;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

            if (response.statusCode() == 200) {
                Genre genre = parseGenreFromJson(response.body());
                return Optional.of(genre);
            } else {
                return Optional.empty();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private Genre parseGenreFromJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Genre.class);
    }

}
