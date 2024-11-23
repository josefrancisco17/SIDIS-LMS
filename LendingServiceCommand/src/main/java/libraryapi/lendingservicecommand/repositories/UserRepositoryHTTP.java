package libraryapi.lendingservicecommand.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.cdimascio.dotenv.Dotenv;
import libraryapi.lendingservicecommand.model.User;
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
import java.util.Optional;

@Repository
public class UserRepositoryHTTP {
    @Autowired
    private Environment env;

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final Dotenv dotenv = Dotenv.load();
    private final String token = dotenv.get("TOKEN");
    private final int AuthServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("AUTH_PORT1")));
    private final int AuthServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("AUTH_PORT2")));
    private final int LendingServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("LENDING_PORT1")));
    private final int LendingServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("LENDING_PORT2")));

    public Optional<User> getUserByUsername(String username) {
        int targetPort;
        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        try {
            targetPort = (currentPort == LendingServicePort1) ? AuthServicePort1 : AuthServicePort2;
        } catch (NumberFormatException | NullPointerException e) {
            throw new RuntimeException("Invalid or missing server port: " + e.getMessage(), e);
        }

        Optional<User> user;

        try {
            String url = "http://localhost:" + targetPort + "/api/public/internal" + username;

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

                user = objectMapper.readValue(response.body(), new TypeReference<Optional<User>>() {});
            } else {
                throw new RuntimeException("Failed to fetch books: " + response.statusCode());
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return user;
    }
}
