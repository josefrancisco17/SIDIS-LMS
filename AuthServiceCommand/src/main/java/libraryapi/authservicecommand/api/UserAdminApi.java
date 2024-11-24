package libraryapi.authservicecommand.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import libraryapi.authservicecommand.model.Role;
import libraryapi.authservicecommand.model.User;
import libraryapi.authservicecommand.services.*;

import java.util.List;


@Tag(name = "UserAdmin")
@RestController
@RequestMapping(path = "api/admin/user")
@RolesAllowed(Role.ADMIN)
@RequiredArgsConstructor
public class UserAdminApi {

	private final UserService userService;
	private final UserViewMapper userViewMapper;

	@PostMapping
	public UserView create(@RequestBody @Valid final CreateUserRequest request) {
		final var user = userService.create(request);
		return userViewMapper.toUserView(user);
	}

	@PutMapping("{id}")
	public UserView update(@PathVariable final Long id, @RequestBody @Valid final EditUserRequest request) {
		final var user = userService.update(id, request);
		return userViewMapper.toUserView(user);
	}

	@DeleteMapping("{id}")
	public UserView delete(@PathVariable final Long id) {
		final var user = userService.delete(id);
		return userViewMapper.toUserView(user);
	}

	@PostMapping("search")
	public ListResponse<UserView> search(@RequestBody final SearchRequest<SearchUsersQuery> request) {
		final List<User> searchUsers = userService.searchUsers(request.getPage(), request.getQuery());
		return new ListResponse<>(userViewMapper.toUserView(searchUsers));
	}
}
