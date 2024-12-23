package libraryapi.recommendationservicecommand.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table
public class TempLending {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    @Getter
    private Long id;

    @NotNull
    @Column(length = 255)
    @Setter
    @Getter
    private String lendingCode;

    @Version
    @Setter
    @Getter
    private long version;

    @NotNull
    @Setter
    @Getter
    @Column(length = 255)
    private Long readerId;

    @NotNull
    @Setter
    @Getter
    @Column(length = 255)
    private Long bookId;

    @NotNull
    @Setter
    @Getter
    @Column(length = 255)
    private String bookTitle;

    @NotNull
    @Setter
    @Getter
    private LocalDate lendDate;

    @NotNull
    @Setter
    @Getter
    private LocalDate limitDate;

    @Setter
    @Getter
    private LocalDate returnedDate;

    @Setter
    @Getter
    private Integer daysTillReturn;

    @Setter
    @Getter
    private boolean returned;

    @Setter
    @Getter
    private Integer daysOverdue;

    @Setter
    @Getter
    private Float fine;

    @Setter
    @Getter
    private String comment;

    @Setter
    @Getter
    private Boolean recommended;

    public TempLending(Lending lending, Boolean recommended) {
        this.id = lending.getId();
        this.lendingCode = lending.getLendingCode();
        this.version = lending.getVersion();
        this.readerId = lending.getReaderId();
        this.bookId = lending.getBookId();
        this.bookTitle = lending.getBookTitle();
        this.lendDate = lending.getLendDate();
        this.limitDate = lending.getLimitDate();
        this.returnedDate = lending.getReturnedDate();
        this.daysTillReturn = lending.getDaysTillReturn();
        this.returned = lending.isReturned();
        this.daysOverdue = lending.getDaysOverdue();
        this.fine = lending.getFine();
        this.comment = lending.getComment();
        this.recommended = recommended;
    }

    public TempLending() {

    }

    @Override
    public String toString() {
        return "TempLending{" +
                "id=" + id +
                ", lendingCode='" + lendingCode + '\'' +
                ", bookId=" + bookId +
                ", bookTitle='" + bookTitle + '\'' +
                ", lendDate=" + lendDate +
                ", limitDate=" + limitDate +
                ", returnedDate=" + returnedDate +
                ", daysTillReturn=" + daysTillReturn +
                ", returned=" + returned +
                ", daysOverdue=" + daysOverdue +
                ", fine=" + fine +
                ", comment='" + comment + '\'' +
                ", readerId=" + readerId +
                ", recommended=" + recommended +
                '}';
    }
}
