package libraryapi.bookservicecommand.api;

import libraryapi.bookservicecommand.rabbitMQ.producer.Sender;
import libraryapi.bookservicecommand.repositories.LendingRepository;
import org.mapstruct.Mapper;
import libraryapi.bookservicecommand.model.Book;
import libraryapi.bookservicecommand.model.Lending;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import static libraryapi.bookservicecommand.api.BookGenreViewMapper.toBookGenreView;

@Mapper(componentModel = "spring")
public class LentBookViewMapper {

    @Autowired
    private Sender sender;
    @Autowired
    private LendingRepository lendingRepository;

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
        lentBookView.setAuthorViews(book.getAuthors());

        return lentBookView;
    }
    public Iterable<LentBookView> toLentBookView(Iterable<Book> books) {
        Iterable<Lending> lendings =  lendingRepository.findAll();

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
