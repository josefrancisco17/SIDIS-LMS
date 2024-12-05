package libraryapi.bookservicecommand.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LendingTest {

    @Test
    void getId() {
        Lending lending = new Lending();
        lending.setId(1L);
        assertEquals(1L, lending.getId());
    }

    @Test
    void setId() {
        Lending lending = new Lending();
        lending.setId(2L);
        assertEquals(2L, lending.getId());
    }

    @Test
    void getLendingCode() {
        Lending lending = new Lending();
        lending.setLendingCode("ABC123");
        assertEquals("ABC123", lending.getLendingCode());
    }

    @Test
    void setLendingCode() {
        Lending lending = new Lending();
        lending.setLendingCode("XYZ789");
        assertEquals("XYZ789", lending.getLendingCode());
    }

    @Test
    void getVersion() {
        Lending lending = new Lending();
        lending.setVersion(1L);
        assertEquals(1L, lending.getVersion());
    }

    @Test
    void setVersion() {
        Lending lending = new Lending();
        lending.setVersion(2L);
        assertEquals(2L, lending.getVersion());
    }

    @Test
    void getReaderId() {
        Lending lending = new Lending();
        lending.setReaderId(1L);
        assertEquals(1L, lending.getReaderId());
    }

    @Test
    void setReaderId() {
        Lending lending = new Lending();
        lending.setReaderId(2L);
        assertEquals(2L, lending.getReaderId());
    }

    @Test
    void getBookId() {
        Lending lending = new Lending();
        lending.setBookId(100L);
        assertEquals(100L, lending.getBookId());
    }

    @Test
    void setBookId() {
        Lending lending = new Lending();
        lending.setBookId(200L);
        assertEquals(200L, lending.getBookId());
    }

    @Test
    void getBookTitle() {
        Lending lending = new Lending();
        lending.setBookTitle("Java Programming");
        assertEquals("Java Programming", lending.getBookTitle());
    }

    @Test
    void setBookTitle() {
        Lending lending = new Lending();
        lending.setBookTitle("Effective Java");
        assertEquals("Effective Java", lending.getBookTitle());
    }

    @Test
    void getLendDate() {
        Lending lending = new Lending();
        lending.setLendDate(LocalDate.of(2024, 12, 1));
        assertEquals(LocalDate.of(2024, 12, 1), lending.getLendDate());
    }

    @Test
    void setLendDate() {
        Lending lending = new Lending();
        lending.setLendDate(LocalDate.of(2024, 12, 10));
        assertEquals(LocalDate.of(2024, 12, 10), lending.getLendDate());
    }

    @Test
    void getLimitDate() {
        Lending lending = new Lending();
        lending.setLimitDate(LocalDate.of(2024, 12, 15));
        assertEquals(LocalDate.of(2024, 12, 15), lending.getLimitDate());
    }

    @Test
    void setLimitDate() {
        Lending lending = new Lending();
        lending.setLimitDate(LocalDate.of(2024, 12, 20));
        assertEquals(LocalDate.of(2024, 12, 20), lending.getLimitDate());
    }

    @Test
    void getReturnedDate() {
        Lending lending = new Lending();
        lending.setReturnedDate(LocalDate.of(2024, 12, 18));
        assertEquals(LocalDate.of(2024, 12, 18), lending.getReturnedDate());
    }

    @Test
    void setReturnedDate() {
        Lending lending = new Lending();
        lending.setReturnedDate(LocalDate.of(2024, 12, 25));
        assertEquals(LocalDate.of(2024, 12, 25), lending.getReturnedDate());
    }


    @Test
    void setDaysTillReturn() {
        Lending lending = new Lending();
        lending.setDaysTillReturn(5);
        assertEquals(5, lending.getDaysTillReturn());
    }

    @Test
    void isReturned() {
        Lending lending = new Lending();
        lending.setReturned(true);
        assertTrue(lending.isReturned());
    }

    @Test
    void setReturned() {
        Lending lending = new Lending();
        lending.setReturned(false);
        assertFalse(lending.isReturned());
    }

    @Test
    void getDaysOverdue() {
        Lending lending = new Lending("code123", 1L, 100L, "Java Programming", LocalDate.of(2024, 12, 1), LocalDate.of(2024, 12, 10), null, false, 5.0f, "Late");
        assertEquals(0, lending.getDaysOverdue());
    }

    @Test
    void setDaysOverdue() {
        Lending lending = new Lending();
        lending.setDaysOverdue(3);
        assertEquals(3, lending.getDaysOverdue());
    }

    @Test
    void getFine() {
        Lending lending = new Lending();
        lending.setFine(10.0f);
        assertEquals(10.0f, lending.getFine());
    }

    @Test
    void setFine() {
        Lending lending = new Lending();
        lending.setFine(5.0f);
        assertEquals(5.0f, lending.getFine());
    }

    @Test
    void getComment() {
        Lending lending = new Lending();
        lending.setComment("Late return");
        assertEquals("Late return", lending.getComment());
    }

    @Test
    void setComment() {
        Lending lending = new Lending();
        lending.setComment("No comment");
        assertEquals("No comment", lending.getComment());
    }

    @Test
    void testToString() {
        Lending lending = new Lending("code123", 1L, 100L, "Java", LocalDate.of(2024, 12, 1), LocalDate.of(2024, 12, 10), null, false, 5.0f, "Late");
        String result = lending.toString();
        assertTrue(result.contains("code123"));
        assertTrue(result.contains("Java"));
        assertTrue(result.contains("2024-12-01"));
    }
}
