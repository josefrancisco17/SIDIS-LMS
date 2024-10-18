package libraryapi.authservice.repositories;

import libraryapi.authservice.model.User;
import libraryapi.authservice.services.Page;
import libraryapi.authservice.services.SearchUsersQuery;

import java.util.List;

interface UserRepoCustom {
    List<User> searchUsers(Page page, SearchUsersQuery query);
}
