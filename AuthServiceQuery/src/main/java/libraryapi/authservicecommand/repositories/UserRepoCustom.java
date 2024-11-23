package libraryapi.authservicecommand.repositories;

import libraryapi.authservicecommand.model.User;
import libraryapi.authservicecommand.services.Page;
import libraryapi.authservicecommand.services.SearchUsersQuery;

import java.util.List;

interface UserRepoCustom {
    List<User> searchUsers(Page page, SearchUsersQuery query);
}
