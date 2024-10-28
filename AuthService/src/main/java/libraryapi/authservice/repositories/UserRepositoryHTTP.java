package libraryapi.authservice.repositories;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.cdimascio.dotenv.Dotenv;
import libraryapi.authservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

@Repository
public class UserRepositoryHTTP {
    @Autowired
    private Environment env;

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final Dotenv dotenv = Dotenv.load();
    private final String token = dotenv.get("TOKEN");
    private final int AuthServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("AUTH_PORT1")));
    private final int AuthServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("AUTH_PORT2")));

    public void manageInternalUser(User user) {
        int targetPort;
        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        try {
            targetPort = (currentPort == AuthServicePort1) ? AuthServicePort2 :AuthServicePort1;
        } catch (NumberFormatException | NullPointerException e) {
            throw new RuntimeException("Invalid or missing server port: " + e.getMessage(), e);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .registerModule(new JavaTimeModule());

            String userJson = objectMapper.writeValueAsString(user);

            String url = "http://localhost:" + targetPort + "/api/public/internal";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(userJson))
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .build();

            System.out.println("Request Body: " + userJson);
            System.out.println("Request URL: " + url);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Body: " + response.body());

        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
