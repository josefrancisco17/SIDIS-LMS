package libraryapi.authservicequery.repositories;

import libraryapi.authservicequery.model.User;
import libraryapi.authservicequery.services.Page;
import libraryapi.authservicequery.services.SearchUsersQuery;

import java.util.List;

interface UserRepoCustom {
    List<User> searchUsers(Page page, SearchUsersQuery query);
}
