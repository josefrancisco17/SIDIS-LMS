package libraryapi.readerservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import libraryapi.readerservice.model.BookCover;
import libraryapi.readerservice.model.Genre;
import libraryapi.readerservice.util.BookUtil;
import org.hibernate.StaleObjectStateException;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private long version;

    @Column(length = 13, nullable = false, unique = true, updatable = false)
    @NotEmpty
    private String isbn;

    @Column(length = 255, nullable = false)
    @NotEmpty
    private String title;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "genreId")
    private Genre genre;

    @Column(length = 4096, nullable = true)
    private String description;

    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    private BookCover cover;

    public Book() {
    }

    public Book(String isbn, String title, Genre genre, String description, List<Author> authors, BookCover cover) {
        this.isbn = isbn;
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.authors = authors;
        this.cover = cover;
    }

    public Book(String isbn, String title, Genre genre, List<Author> authors, String description) {
        this.isbn = isbn;
        this.title = title;
        this.genre = genre;
        this.description = description;
    }

    public Book(String isbn, String title, Genre genre, String description) {
        this.isbn = isbn;
        this.title = title;
        this.genre = genre;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        if (!BookUtil.isValidISBN(isbn)) {
            throw new IllegalArgumentException("[ERROR] Isbn is no valid.");
        }
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null && title.isBlank() || title.length() > 255) {
            throw new IllegalArgumentException("[ERROR] Title is not valid.");
        }

        this.title = title;
    }

    public Genre getGenre() {
        if (genre == null) {
            throw new IllegalArgumentException("[ERROR] Genre is not valid.");
        }
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public String getDescription() {
        if (genre == null) {
            throw new IllegalArgumentException("[ERROR] Genre is not valid.");
        }
        return description;
    }

    public void setDescription(String description) {
        if (description != null && description.length() > 4096) {
            throw new IllegalArgumentException("[ERROR] Description is not valid.");
        }
        this.description = description;
    }

    public BookCover getCover() {
        return cover;
    }

    public void setCover(BookCover cover) {
        this.cover = cover;
    }

    public void updateData(final long desiredVersion, final String title, final List<Author> authors, final Genre genre, final String description) {
        if (this.version != desiredVersion) {
            throw new StaleObjectStateException("Object was already modified by another user", this.id);
        }
        setTitle(title);
        setAuthors(authors);
        setGenre(genre);
        setDescription(description);
    }

    public void applyPatch(final long desiredVersion, final String title, final List<Author> authors, final Genre genre, final String description) {
        if (this.version != desiredVersion) {
            throw new StaleObjectStateException("Object was already modified by another user", this.id);
        }
        if (title != null && !title.isEmpty()) {
            setTitle(title);
        }
        if (genre != null) {
            setGenre(genre);
        }

        if (authors != null && !authors.isEmpty()) {
            setAuthors(authors);
        }

        if (description != null) {
            if (description.isEmpty()) {
                setDescription(null);
            } else {
                setDescription(description);
            }
        } else {
            setDescription(null);
        }
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", description='" + description + '\'' +
                ", authors=" + authors +
                '}';
    }
}
