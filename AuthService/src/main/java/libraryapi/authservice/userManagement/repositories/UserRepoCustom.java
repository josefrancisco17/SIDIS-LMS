package libraryapi.authservice.userManagement.repositories;

import libraryapi.authservice.userManagement.model.User;
import libraryapi.authservice.userManagement.services.Page;
import libraryapi.authservice.userManagement.services.SearchUsersQuery;

import java.util.List;

interface UserRepoCustom {
    List<User> searchUsers(Page page, SearchUsersQuery query);
}
