package libraryapi.bookservicequery.api;

import org.mapstruct.Mapper;
import libraryapi.bookservicequery.model.Book;
import java.util.ArrayList;
import static libraryapi.bookservicequery.api.BookGenreViewMapper.toBookGenreView;

@Mapper(componentModel = "spring")
public class BookViewMapper {
    public BookView toBookView(Book book) {
        if ( book == null ) {
            return null;
        }

        BookView bookView = new BookView();

        bookView.setId( book.getId() );
        bookView.setIsbn( book.getIsbn() );
        bookView.setTitle( book.getTitle() );
        bookView.setGenre( toBookGenreView(book.getGenre()) );
        bookView.setDescription( book.getDescription() );
        bookView.setAuthors(book.getAuthors());

        return bookView;
    }

    public Iterable<BookView> toBookView(Iterable<Book> books) {
        if ( books == null ) {
            return null;
        }

        ArrayList<BookView> iterable = new ArrayList<BookView>();
        for ( Book book : books ) {
            iterable.add( toBookView( book ) );
        }

        return iterable;
    }

    public BookView toCreateBookView(Book book) {
        if ( book == null ) {
            return null;
        }

        BookView bookView = new BookView();

        bookView.setId( book.getId() );
        bookView.setIsbn( book.getIsbn() );
        bookView.setTitle( book.getTitle() );
        bookView.setGenre( toBookGenreView(book.getGenre()) );
        bookView.setDescription( book.getDescription() );
        bookView.setAuthors(new ArrayList<>());

        return bookView;
    }
}

