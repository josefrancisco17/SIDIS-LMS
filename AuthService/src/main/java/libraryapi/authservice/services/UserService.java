package libraryapi.authservice.services;

import jakarta.validation.ValidationException;
import libraryapi.authservice.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import libraryapi.authservice.exceptions.ConflictException;
import libraryapi.authservice.model.User;
import libraryapi.authservice.repositories.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepo;
	private final EditUserMapper userEditMapper;

	private final PasswordEncoder passwordEncoder;

	@Transactional
	public User create(final CreateUserRequest request) {
		if (userRepo.findByUsername(request.getUsername()).isPresent()) {
			throw new ConflictException("Username already exists!");
		}
		if (!request.getPassword().equals(request.getRePassword())) {
			throw new ValidationException("Passwords don't match!");
		}

		final User user = userEditMapper.create(request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		return userRepo.save(user);
	}

	@Transactional
	public User update(final Long id, final EditUserRequest request) {
		final User user = userRepo.getById(id);
		userEditMapper.update(request, user);

		return userRepo.save(user);
	}

	@Transactional
	public User upsert(final CreateUserRequest request) {
		final Optional<User> optionalUser = userRepo.findByUsername(request.getUsername());

		if (optionalUser.isEmpty()) {
			return create(request);
		}
		final EditUserRequest updateUserRequest = new EditUserRequest(request.getFullName(), request.getAuthorities());
		return update(optionalUser.get().getId(), updateUserRequest);
	}

	@Transactional
	public User delete(final Long id) {
		final User user = userRepo.getById(id);

		// user.setUsername(user.getUsername().replace("@", String.format("_%s@",
		// user.getId().toString())));
		user.setEnabled(false);
		return userRepo.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		return userRepo.findByUsername(username).orElseThrow(
				() -> new UsernameNotFoundException(String.format("User with username - %s, not found", username)));
	}

	public boolean usernameExists(final String username) {
		return userRepo.findByUsername(username).isPresent();
	}

	public User getUser(final Long id) {
		return userRepo.getById(id);
	}

	public List<User> searchUsers(Page page, SearchUsersQuery query) {
		if (page == null) {
			page = new Page(1, 10);
		}
		if (query == null) {
			query = new SearchUsersQuery("", "");
		}
		return userRepo.searchUsers(page, query);
	}

	public Optional<User> getUserByUsername(String username) {
		return userRepo.findByUsername(username);
	}
}
