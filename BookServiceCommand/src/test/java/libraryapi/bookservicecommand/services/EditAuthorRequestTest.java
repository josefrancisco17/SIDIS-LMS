package libraryapi.bookservicecommand.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EditAuthorRequestTest {

    private EditAuthorRequest editAuthorRequest;

    @BeforeEach
    void setUp() {
        editAuthorRequest = new EditAuthorRequest();
    }

    @Test
    void getName() {
        editAuthorRequest.setName("John Doe");
        assertEquals("John Doe", editAuthorRequest.getName(), "O nome deveria ser 'John Doe'");
    }

    @Test
    void getShortBio() {
        String bio = "John Doe é um autor de ficção com diversos livros publicados.";
        editAuthorRequest.setShortBio(bio);
        assertEquals(bio, editAuthorRequest.getShortBio(), "A biografia curta deveria coincidir com o valor definido.");
    }

    @Test
    void setName() {
        String name = "Jane Doe";
        editAuthorRequest.setName(name);
        assertEquals(name, editAuthorRequest.getName(), "O nome deveria ser 'Jane Doe'");
    }

    @Test
    void setShortBio() {
        String bio = "Jane Doe é uma autora premiada de romances de mistério.";
        editAuthorRequest.setShortBio(bio);
        assertEquals(bio, editAuthorRequest.getShortBio(), "A biografia curta deveria ser 'Jane Doe é uma autora premiada de romances de mistério.'");
    }

    @Test
    void testEquals() {
        EditAuthorRequest anotherRequest = new EditAuthorRequest("John Doe", "Autor famoso");
        editAuthorRequest.setName("John Doe");
        editAuthorRequest.setShortBio("Autor famoso");

        assertEquals(anotherRequest, editAuthorRequest, "As instâncias deveriam ser iguais");
    }

    @Test
    void canEqual() {
        EditAuthorRequest anotherRequest = new EditAuthorRequest("Jane Doe", "Autora de romances");
        assertTrue(editAuthorRequest.canEqual(anotherRequest), "O método canEqual deveria retornar true para instâncias da mesma classe.");
    }

    @Test
    void testHashCode() {
        EditAuthorRequest anotherRequest = new EditAuthorRequest("John Doe", "Autor famoso");
        editAuthorRequest.setName("John Doe");
        editAuthorRequest.setShortBio("Autor famoso");

        assertEquals(anotherRequest.hashCode(), editAuthorRequest.hashCode(), "Os hashCodes deveriam ser iguais para objetos com os mesmos valores.");
    }

    @Test
    void testToString() {
        editAuthorRequest.setName("John Doe");
        editAuthorRequest.setShortBio("Autor famoso");

        String expected = "EditAuthorRequest(name=John Doe, shortBio=Autor famoso)";
        assertEquals(expected, editAuthorRequest.toString(), "O resultado de toString deveria coincidir com o formato esperado.");
    }
}
