package libraryapi.authservicequery.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import libraryapi.authservicequery.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import libraryapi.authservicequery.model.User;
import libraryapi.authservicequery.services.CreateUserRequest;
import libraryapi.authservicequery.services.UserService;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;


@Tag(name = "Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/public")
public class AuthApi {

	private final AuthenticationManager authenticationManager;

	private final JwtEncoder jwtEncoder;

	private final UserViewMapper userViewMapper;

	private final UserService userService;

	@Operation(summary = "Get user data given its bearer token")
	@GetMapping("/internal/{username}")
	public Optional<User> getUserByUsername(@PathVariable("username") String username) {
		Optional<User> user = userService.getUserByUsername(username);
		if (user.isEmpty()) {
			throw new NotFoundException(User.class, username);
		}
		return user;
	}
}
