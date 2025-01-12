package libraryapi.recommendationservicecommand.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    @Getter
    private Long id;

    @NotNull
    @Getter
    @Setter
    @Column(length = 255)
    private String lendingCode;

    @NotNull
    @Getter
    @Setter
    private Boolean recommended;

    @Override
    public String toString() {
        return "Recommendation{" +
                "id=" + id +
                ", lendingCode='" + lendingCode + '\'' +
                ", recommended=" + recommended +
                '}';
    }
}
