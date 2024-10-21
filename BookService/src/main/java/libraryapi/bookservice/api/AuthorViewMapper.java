package libraryapi.bookservice.api;

import libraryapi.bookservice.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public class AuthorViewMapper {
    public AuthorView toAuthorView(Author author) {
        if (author == null) {
            return null;
        }

        AuthorView authorView = new AuthorView();
        authorView.setId(author.getId());
        authorView.setName(author.getName());
        authorView.setShortBio(author.getShortBio());
        return authorView;
    }

    public Iterable<AuthorView> toAuthorView(Iterable<Author> authors) {
        List<AuthorView> authorViews = new ArrayList<>();
        for (Author author : authors) {
            authorViews.add(toAuthorView(author));
        }
        return authorViews;
    }
}
