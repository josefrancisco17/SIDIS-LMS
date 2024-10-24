package libraryapi.authservice.api;

import org.mapstruct.Mapper;
import libraryapi.authservice.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserViewMapper {

	public abstract UserView toUserView(User user);

	public abstract List<UserView> toUserView(List<User> users);
}
