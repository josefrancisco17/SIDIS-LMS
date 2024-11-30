package libraryapi.readerservicecommand.repositories;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

import libraryapi.readerservicecommand.model.Reader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

@Repository
public class ReaderRepositoryHTTP {
    /*
    @Autowired
    private Environment env;

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final Dotenv dotenv = Dotenv.load();
    private final String token = dotenv.get("TOKEN");
    private final int ReaderServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("READER_PORT1")));
    private final int ReaderServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("READER_PORT2")));

    public void manageInternalReader(Reader reader) {
        int targetPort;
        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        try {
            targetPort = (currentPort == ReaderServicePort1) ? ReaderServicePort2 :ReaderServicePort1;
        } catch (NumberFormatException | NullPointerException e) {
            throw new RuntimeException("Invalid or missing server port: " + e.getMessage(), e);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .registerModule(new JavaTimeModule());

            String readerJson = objectMapper.writeValueAsString(reader);

            String url = "http://localhost:" + targetPort + "/api/readers/internal";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(readerJson))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();

            System.out.println("Request Body: " + readerJson);
            System.out.println("Request URL: " + url);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Body: " + response.body());

        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

     */
}
