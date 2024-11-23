package libraryapi.bookservicecommand.configuration;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import libraryapi.bookservicecommand.model.Role;
import libraryapi.bookservicecommand.repositories.UserRepositoryHTTP;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static java.lang.String.format;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@EnableConfigurationProperties
@RequiredArgsConstructor
public class SecurityConfig {

	private final UserRepositoryHTTP userRepositoryHTTP;

	@Value("${jwt.public.key}")
	private RSAPublicKey rsaPublicKey;

	@Value("${jwt.private.key}")
	private RSAPrivateKey rsaPrivateKey;

	@Value("${springdoc.api-docs.path}")
	private String restApiDocPath;

	@Value("${springdoc.swagger-ui.path}")
	private String swaggerPath;

	@Bean
	public AuthenticationManager authenticationManager(final UserDetailsService userDetailsService,
													   final PasswordEncoder passwordEncoder) {
		final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);

		return new ProviderManager(authenticationProvider);
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> userRepositoryHTTP.getUserByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(format("User: %s, not found", username)));
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// Enable CORS and disable CSRF
		http = http.cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable());

		// Set session management to stateless
		http = http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Set unauthorized requests exception handler
		http = http.exceptionHandling(
				exceptions -> exceptions.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
						.accessDeniedHandler(new BearerTokenAccessDeniedHandler()));

		// Set permissions on endpoints
		http.authorizeHttpRequests()
				// Swagger endpoints must be publicly accessible
				.requestMatchers("/").permitAll().requestMatchers(format("%s/**", restApiDocPath)).permitAll()
				.requestMatchers(format("%s/**", swaggerPath)).permitAll()
				// Our public endpoints
				.requestMatchers("/api/public/**").permitAll() // public assets & end-points
				.requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/books/**").permitAll()
				.requestMatchers(HttpMethod.PUT, "/api/books/**").permitAll()
				.requestMatchers(HttpMethod.PATCH, "/api/books/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/lendings/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/lendings/**").permitAll()
				.requestMatchers(HttpMethod.PUT, "/api/lendings/**").permitAll()
				.requestMatchers(HttpMethod.PATCH, "/api/lendings/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/authors/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/authors/**").permitAll()
				.requestMatchers(HttpMethod.PUT, "/api/authors/**").permitAll()
				.requestMatchers(HttpMethod.PATCH, "/api/authors/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/readers/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/readers/**").permitAll()
				.requestMatchers(HttpMethod.PUT, "/api/readers/**").permitAll()
				.requestMatchers(HttpMethod.PATCH, "/api/readers/**").permitAll()
				// Our private endpoints
				.requestMatchers("/api/admin/user/**").hasRole(Role.ADMIN)
				.requestMatchers("/api/books/**").hasRole(Role.ADMIN)
				.requestMatchers("/api/lendings/**").hasRole(Role.ADMIN)
				.requestMatchers("/api/authors/**").hasRole(Role.ADMIN)
				.requestMatchers("/api/readers/**").hasRole(Role.ADMIN)
				.requestMatchers("/h2-console/**").permitAll()
				.anyRequest().authenticated()
				// Set up oauth2 resource server
				.and().httpBasic(Customizer.withDefaults()).oauth2ResourceServer().jwt();
		http.headers(headers -> headers.frameOptions().sameOrigin());

		return http.build();
	}

	// Used by JwtAuthenticationProvider to generate JWT tokens
	@Bean
	public JwtEncoder jwtEncoder() {
		final JWK jwk = new RSAKey.Builder(this.rsaPublicKey).privateKey(this.rsaPrivateKey).build();
		final JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks);
	}

	// Used by JwtAuthenticationProvider to decode and validate JWT tokens
	@Bean
	public JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withPublicKey(this.rsaPublicKey).build();
	}

	// Extract authorities from the roles claim
	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

		final JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}

	// Set password encoding schema
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Used by spring security if CORS is enabled.
	@Bean
	public CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

}