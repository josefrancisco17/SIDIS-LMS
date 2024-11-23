package libraryapi.bookservicequery.services;

import jakarta.validation.constraints.Size;
import libraryapi.bookservicequery.model.Author;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import libraryapi.bookservicequery.model.Genre;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditBookRequest {
    @Size(min = 1, max = 256)
    private String title;

    private Genre genre;

    @Size(min = 0, max = 4096)
    private String description;

    private List<Author> Authors;
}
