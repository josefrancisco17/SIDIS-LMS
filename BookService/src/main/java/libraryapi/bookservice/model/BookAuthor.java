package libraryapi.bookservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import libraryapi.bookservice.model.Book;

@Entity
@Table
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class BookAuthor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bookId")
    @NotNull
    private Book book;

    @ManyToOne
    @JoinColumn(name = "authorId")
    @NotNull
    private Author author;

    public BookAuthor() {
    }

    public BookAuthor(Book book, Author author) {
        this.book = book;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "BookAuthor{" +
                "id=" + id +
                ", bookId=" + (book != null ? book.getId() : null) +
                ", authorId=" + (author != null ? author.getId() : null) +
                '}';
    }
}
