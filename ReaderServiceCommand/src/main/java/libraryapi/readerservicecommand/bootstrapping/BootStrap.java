package libraryapi.readerservicecommand.bootstrapping;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import libraryapi.readerservicecommand.model.Reader;
import libraryapi.readerservicecommand.repositories.ReaderRepository;

import java.time.LocalDate;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Profile("bootstrap")
public class BootStrap implements CommandLineRunner {
    private final ReaderRepository readerRepository;

    @Override
    public void run(String... args) {
        // Reader Mock Data
        Reader reader1 = new Reader("2024/1", "Ricardo Costa", "ricardocosta@gmail.com", LocalDate.of(1998, 2, 27), 910787100, true, Arrays.asList("Science Fiction", "Fantasy"));
        Reader reader2 = new Reader("2024/2", "Joao Fonsenca", "joao1236@isep.ipp.pt", LocalDate.of(2000, 9, 20), 987654321, true, Arrays.asList("Mystery", "Thriller"));
        Reader reader3 = new Reader("2024/3", "Victor Barbosa", "victorbarbosa@gmail.com", LocalDate.of(2005, 1, 11), 933342550, true, null);
        Reader reader4 = new Reader("2024/4", "Guilherme Gouveia", "guilhermeGouveia@outlook.com", LocalDate.of(1969, 3, 12), 917584125, true, Arrays.asList("Romance", "Non-Fiction"));
        Reader reader5 = new Reader("2024/5", "Tiago Martins", "tiagomartins@gmail.com", LocalDate.of(1978, 3, 15), 916625978, true, Arrays.asList("Biography", "History"));
        Reader reader6 = new Reader("2024/6", "Ana Silva", "ana.silva@gmail.com", LocalDate.of(1985, 5, 25), 912345678, true, Arrays.asList("Children's", "Self-Improvement"));
        Reader reader7 = new Reader("2024/7", "Carlos Santos", "carlos.santos@hotmail.com", LocalDate.of(1992, 8, 10), 913456789, true, null);
        Reader reader8 = new Reader("2024/8", "Mariana Pereira", "mariana.pereira@yahoo.com", LocalDate.of(1980, 11, 22), 914567890, true, Arrays.asList("Science Fiction", "Mystery"));
        Reader reader9 = new Reader("2024/9", "José Ferreira", "jose.ferreira@gmail.com", LocalDate.of(1975, 6, 30), 915678901, true, null);
        Reader reader10 = new Reader("2024/10", "Sara Gomes", "sara.gomes@outlook.com", LocalDate.of(1999, 7, 18), 916789012, true, Arrays.asList("Fantasy", "Romance"));
        Reader reader11 = new Reader("2024/11", "Miguel Rocha", "miguel.rocha@gmail.com", LocalDate.of(2002, 2, 2), 917890123, true, null);
        Reader reader12 = new Reader("2024/12", "Luisa Marques", "luisa.marques@isep.ipp.pt", LocalDate.of(1994, 12, 15), 918901234, true, Arrays.asList("Non-Fiction", "Thriller"));
        Reader reader13 = new Reader("2024/13", "Rui Almeida", "rui.almeida@gmail.com", LocalDate.of(1988, 4, 27), 919012345, true, null);
        Reader reader14 = new Reader("2024/14", "Beatriz Sousa", "beatriz.sousa@hotmail.com", LocalDate.of(1970, 9, 9), 910123456, true, Arrays.asList("Biography", "Self-Improvement"));
        Reader reader15 = new Reader("2024/15", "Pedro Mendes", "pedro.mendes@yahoo.com", LocalDate.of(1991, 1, 19), 911234567, true, Arrays.asList("Children's", "Science Fiction"));
        Reader reader16 = new Reader("2024/16", "Clara Oliveira", "clara.oliveira@gmail.com", LocalDate.of(1983, 8, 5), 912345678, true, null);
        Reader reader17 = new Reader("2024/17", "André Ribeiro", "andre.ribeiro@outlook.com", LocalDate.of(1995, 3, 14), 913456789, true, Arrays.asList("Mystery", "Fantasy"));
        Reader reader18 = new Reader("2024/18", "Teresa Lopes", "teresa.lopes@gmail.com", LocalDate.of(2001, 7, 29), 914567890, true, Arrays.asList("Romance", "Non-Fiction"));
        Reader reader19 = new Reader("2024/19", "Hugo Cunha", "hugo.cunha@gmail.com", LocalDate.of(1972, 10, 21), 915678901, true, Arrays.asList("Thriller", "Biography"));
        Reader reader20 = new Reader("2024/20", "Marta Faria", "marta.faria@yahoo.com", LocalDate.of(1986, 12, 3), 916789012, true, Arrays.asList("History", "Self-Improvement"));

        readerRepository.saveAll(Arrays.asList(
                reader1, reader2, reader3, reader4, reader5, reader6, reader7, reader8, reader9, reader10,
                reader11, reader12, reader13, reader14, reader15, reader16, reader17, reader18, reader19, reader20
        ));

    }
}
