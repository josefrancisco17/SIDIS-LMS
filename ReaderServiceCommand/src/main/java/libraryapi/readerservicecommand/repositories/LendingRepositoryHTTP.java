package libraryapi.readerservicecommand.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.cdimascio.dotenv.Dotenv;
import libraryapi.readerservicecommand.model.Lending;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;

@Repository
public class LendingRepositoryHTTP {
    /*
    @Autowired
    private Environment env;

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final Dotenv dotenv = Dotenv.load();
    private final String token = dotenv.get("TOKEN");
    private final int LendingServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("LENDING_PORT1")));
    private final int LendingServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("LENDING_PORT2")));
    private final int ReaderServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("READER_PORT1")));
    private final int ReaderServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("READER_PORT2")));

    public List<Lending> getAllLendings() {
        int targetPort;
        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        try {
            targetPort = (currentPort == ReaderServicePort1) ? LendingServicePort1 : LendingServicePort2;
        } catch (NumberFormatException | NullPointerException e) {
            throw new RuntimeException("Invalid or missing server port: " + e.getMessage(), e);
        }

        List<Lending> lendings;

        try {
            String url = "http://localhost:" + targetPort + "/api/lendings/internal";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            System.out.println("Request URL: " + url);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Body: " + response.body());

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

     */
}
