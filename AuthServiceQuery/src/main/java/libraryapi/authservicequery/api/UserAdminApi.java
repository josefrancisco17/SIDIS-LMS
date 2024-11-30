package libraryapi.authservicequery.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import libraryapi.authservicequery.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import libraryapi.authservicequery.model.Role;
import libraryapi.authservicequery.model.User;
import libraryapi.authservicequery.services.*;

import java.util.List;


@Tag(name = "UserAdmin")
@RestController
@RequestMapping(path = "api/admin/user")
@RolesAllowed(Role.ADMIN)
@RequiredArgsConstructor
public class UserAdminApi {

	private final UserService userService;
	private final UserViewMapper userViewMapper;

	@GetMapping("{id}")
	public UserView get(@PathVariable final Long id) {
		final var user = userService.getUser(id);
		return userViewMapper.toUserView(user);
	}

}
