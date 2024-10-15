package libraryapi.bookservice.bookManagement.api;

import org.mapstruct.Mapper;
import libraryapi.bookservice.bookManagement.model.Book;
import libraryapi.bookservice.bookManagement.model.BookAuthor;
import psoftg2.libraryapi.lendingManagement.model.Lending;

import java.util.ArrayList;
import java.util.List;

import static psoftg2.libraryapi.bookManagement.api.BookAuthorViewMapper.toBookAuthorView;
import static psoftg2.libraryapi.bookManagement.api.BookGenreViewMapper.toBookGenreView;

@Mapper(componentModel = "spring")
public class LentBookViewMapper {
    public LentBookView toLentBookView(Book book, Iterable<Lending> lendings) {
        if ( book == null ) {
            return null;
        }

        LentBookView lentBookView = new LentBookView();


        List<Lending> lendingsList = new ArrayList<>();
        for (Lending lending : lendings) {
            lendingsList.add(lending);
        }
        lentBookView.setLentCount(countLentsForBook(book, lendingsList));
        lentBookView.setId( book.getId() );
        lentBookView.setIsbn( book.getIsbn() );
        lentBookView.setTitle( book.getTitle() );
        lentBookView.setGenre( toBookGenreView(book.getGenre()));
        lentBookView.setDescription( book.getDescription() );
        List<BookAuthor> list = book.getBookAuthors();
        if (list != null) {
            List<BookAuthorView> viewList = new ArrayList<>();
            for (BookAuthor bookAuthor : list) {
                viewList.add(toBookAuthorView(bookAuthor));
            }
            lentBookView.setBookAuthors(viewList);
        }

        return lentBookView;
    }
    public Iterable<LentBookView> toLentBookView(Iterable<Book> books, Iterable<Lending> lendings) {
        if ( books == null ) {
            return null;
        }

        ArrayList<LentBookView> iterable = new ArrayList<LentBookView>();
        for ( Book book : books ) {
            iterable.add( toLentBookView(book, lendings));
        }

        return iterable;
    }

    private int countLentsForBook(Book book, List<Lending> lendings) {
        int count = 0;
        for (Lending lending : lendings) {
            if (lending.getBookId().equals(book.getId())) {
                count++;
            }
        }
        return count;
    }
}
