package libraryapi.bookservice.api;

import libraryapi.bookservice.model.BookAuthor;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public class BookAuthorViewMapper {
    private static final Logger log = LoggerFactory.getLogger(BookAuthorViewMapper.class);

    public static BookAuthorView toBookAuthorView(BookAuthor bookAuthor) {
        if (bookAuthor == null) {
            log.warn("Received null BookAuthor");
            return null;
        }

        BookAuthorView bookAuthorView = new BookAuthorView();

        if (bookAuthor.getAuthor() == null) {
            log.warn("BookAuthor has null Author: {}", bookAuthor);
            return bookAuthorView;
        }

        String authorName = bookAuthor.getAuthor().getName();
        if (authorName == null) {
            log.warn("Author has null name: {}", bookAuthor.getAuthor());
            authorName = "Unknown";
        }
        bookAuthorView.setAuthor(authorName);

        String shortBio = bookAuthor.getAuthor().getShortBio();
        if (shortBio == null) {
            log.warn("Author has null shortBio: {}", bookAuthor.getAuthor());
            shortBio = "";
        }
        bookAuthorView.setShortBio(shortBio);

        return bookAuthorView;
    }

    public static List<BookAuthorView> toBookAuthorView(Iterable<BookAuthor> bookAuthors) {
        if (bookAuthors == null) {
            log.warn("Received null bookAuthors collection");
            return null;
        }

        List<BookAuthorView> bookAuthorViews = new ArrayList<>();
        for (BookAuthor bookAuthor : bookAuthors) {
            BookAuthorView view = toBookAuthorView(bookAuthor);
            if (view != null) {
                bookAuthorViews.add(view);
            }
        }

        return bookAuthorViews;
    }
}