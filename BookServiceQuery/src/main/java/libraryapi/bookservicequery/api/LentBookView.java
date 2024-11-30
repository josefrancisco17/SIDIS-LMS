package libraryapi.bookservicequery.api;

import io.swagger.v3.oas.annotations.media.Schema;
import libraryapi.bookservicequery.model.Author;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "LentBookView")
public class LentBookView {
    private int lentCount;
    private Long id;
    private String isbn;
    private String title;
    private BookGenreView genre;
    private String description;
    private List<Author> authorViews;
}

