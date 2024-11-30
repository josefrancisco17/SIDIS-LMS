package libraryapi.authservicecommand.services;

import org.mapstruct.*;
import libraryapi.authservicecommand.model.Role;
import libraryapi.authservicecommand.model.User;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = "spring")
public abstract class EditUserMapper {

	@Mapping(source = "authorities", target = "authorities", qualifiedByName = "stringToRole")
	public abstract User create(CreateUserRequest request);

	@BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "authorities", target = "authorities", qualifiedByName = "stringToRole")
	public abstract void update(EditUserRequest request, @MappingTarget User user);

	@Named("stringToRole")
	protected Set<Role> stringToRole(final Set<String> authorities) {
		if (authorities != null) {
			return authorities.stream().map(Role::new).collect(toSet());
		}
		return new HashSet<>();
	}

}
