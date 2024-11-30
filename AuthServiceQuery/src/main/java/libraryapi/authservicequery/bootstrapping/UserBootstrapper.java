package libraryapi.authservicequery.bootstrapping;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import libraryapi.authservicequery.model.Role;
import libraryapi.authservicequery.model.User;
import libraryapi.authservicequery.repositories.UserRepository;

@Component
@RequiredArgsConstructor
@Profile("bootstrap")
public class UserBootstrapper implements CommandLineRunner {

    @Autowired
    private final UserRepository userRepo;

    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public void run(final String... args) throws Exception {
        if (userRepo.findByUsername("admin1@mail.com").isEmpty()) {
            final User admin1 = new User("admin1@mail.com", encoder.encode("admin"), "Admin");
            admin1.addAuthority(new Role(Role.ADMIN));
            userRepo.save(admin1);
        }
        if (userRepo.findByUsername("ricardocosta@gmail.com").isEmpty()) {
            final User reader1 = new User("ricardocosta@gmail.com", encoder.encode("password"), "Reader");
            reader1.addAuthority(new Role(Role.READER));
            userRepo.save(reader1);
        }

        if (userRepo.findByUsername("joao1236@isep.ipp.pt").isEmpty()) {
            final User reader2 = new User("joao1236@isep.ipp.pt", encoder.encode("password"), "Reader");
            reader2.addAuthority(new Role(Role.READER));
            userRepo.save(reader2);
        }

        if (userRepo.findByUsername("victorbarbosa@gmail.com").isEmpty()) {
            final User reader3 = new User("victorbarbosa@gmail.com", encoder.encode("password"), "Reader");
            reader3.addAuthority(new Role(Role.READER));
            userRepo.save(reader3);
        }

        if (userRepo.findByUsername("guilhermeGouveia@outlook.com").isEmpty()) {
            final User reader4 = new User("guilhermeGouveia@outlook.com", encoder.encode("password"), "Reader");
            reader4.addAuthority(new Role(Role.READER));
            userRepo.save(reader4);
        }

        if (userRepo.findByUsername("tiagomartins@gmail.com").isEmpty()) {
            final User reader5 = new User("tiagomartins@gmail.com", encoder.encode("password"), "Reader");
            reader5.addAuthority(new Role(Role.READER));
            userRepo.save(reader5);
        }

        if (userRepo.findByUsername("ana.silva@gmail.com").isEmpty()) {
            final User reader6 = new User("ana.silva@gmail.com", encoder.encode("password"), "Reader");
            reader6.addAuthority(new Role(Role.READER));
            userRepo.save(reader6);
        }

        if (userRepo.findByUsername("carlos.santos@hotmail.com").isEmpty()) {
            final User reader7 = new User("carlos.santos@hotmail.com", encoder.encode("password"), "Reader");
            reader7.addAuthority(new Role(Role.READER));
            userRepo.save(reader7);
        }

        if (userRepo.findByUsername("mariana.pereira@yahoo.com").isEmpty()) {
            final User reader8 = new User("mariana.pereira@yahoo.com", encoder.encode("password"), "Reader");
            reader8.addAuthority(new Role(Role.READER));
            userRepo.save(reader8);
        }

        if (userRepo.findByUsername("jose.ferreira@gmail.com").isEmpty()) {
            final User reader9 = new User("jose.ferreira@gmail.com", encoder.encode("password"), "Reader");
            reader9.addAuthority(new Role(Role.READER));
            userRepo.save(reader9);
        }

        if (userRepo.findByUsername("sara.gomes@outlook.com").isEmpty()) {
            final User reader10 = new User("sara.gomes@outlook.com", encoder.encode("password"), "Reader");
            reader10.addAuthority(new Role(Role.READER));
            userRepo.save(reader10);
        }

        if (userRepo.findByUsername("miguel.rocha@gmail.com").isEmpty()) {
            final User reader11 = new User("miguel.rocha@gmail.com", encoder.encode("password"), "Reader");
            reader11.addAuthority(new Role(Role.READER));
            userRepo.save(reader11);
        }

        if (userRepo.findByUsername("luisa.marques@isep.ipp.pt").isEmpty()) {
            final User reader12 = new User("luisa.marques@isep.ipp.pt", encoder.encode("password"), "Reader");
            reader12.addAuthority(new Role(Role.READER));
            userRepo.save(reader12);
        }

        if (userRepo.findByUsername("rui.almeida@gmail.com").isEmpty()) {
            final User reader13 = new User("rui.almeida@gmail.com", encoder.encode("password"), "Reader");
            reader13.addAuthority(new Role(Role.READER));
            userRepo.save(reader13);
        }

        if (userRepo.findByUsername("beatriz.sousa@hotmail.com").isEmpty()) {
            final User reader14 = new User("beatriz.sousa@hotmail.com", encoder.encode("password"), "Reader");
            reader14.addAuthority(new Role(Role.READER));
            userRepo.save(reader14);
        }

        if (userRepo.findByUsername("pedro.mendes@yahoo.com").isEmpty()) {
            final User reader15 = new User("pedro.mendes@yahoo.com", encoder.encode("password"), "Reader");
            reader15.addAuthority(new Role(Role.READER));
            userRepo.save(reader15);
        }

        if (userRepo.findByUsername("clara.oliveira@gmail.com").isEmpty()) {
            final User reader16 = new User("clara.oliveira@gmail.com", encoder.encode("password"), "Reader");
            reader16.addAuthority(new Role(Role.READER));
            userRepo.save(reader16);
        }

        if (userRepo.findByUsername("andre.ribeiro@outlook.com").isEmpty()) {
            final User reader17 = new User("andre.ribeiro@outlook.com", encoder.encode("password"), "Reader");
            reader17.addAuthority(new Role(Role.READER));
            userRepo.save(reader17);
        }

        if (userRepo.findByUsername("teresa.lopes@gmail.com").isEmpty()) {
            final User reader18 = new User("teresa.lopes@gmail.com", encoder.encode("password"), "Reader");
            reader18.addAuthority(new Role(Role.READER));
            userRepo.save(reader18);
        }

        if (userRepo.findByUsername("hugo.cunha@gmail.com").isEmpty()) {
            final User reader19 = new User("hugo.cunha@gmail.com", encoder.encode("password"), "Reader");
            reader19.addAuthority(new Role(Role.READER));
            userRepo.save(reader19);
        }

        if (userRepo.findByUsername("marta.faria@yahoo.com").isEmpty()) {
            final User reader20 = new User("marta.faria@yahoo.com", encoder.encode("password"), "Reader");
            reader20.addAuthority(new Role(Role.READER));
            userRepo.save(reader20);
        }

        if (userRepo.findByUsername("librarian1@mail.com").isEmpty()) {
            final var librarian1 = new User("librarian1@mail.com", encoder.encode("password"), "Librarian");
            librarian1.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian1);
        }

        if (userRepo.findByUsername("librarian2@mail.com").isEmpty()) {
            final var librarian2 = new User("librarian2@mail.com", encoder.encode("password"), "Librarian");
            librarian2.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian2);
        }

        if (userRepo.findByUsername("librarian3@mail.com").isEmpty()) {
            final var librarian3 = new User("librarian3@mail.com", encoder.encode("password"), "Librarian");
            librarian3.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian3);
        }

        if (userRepo.findByUsername("librarian4@mail.com").isEmpty()) {
            final var librarian4 = new User("librarian4@mail.com", encoder.encode("password"), "Librarian");
            librarian4.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian4);
        }

        if (userRepo.findByUsername("librarian5@mail.com").isEmpty()) {
            final var librarian5 = new User("librarian5@mail.com", encoder.encode("password"), "Librarian");
            librarian5.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian5);
        }

        if (userRepo.findByUsername("librarian6@mail.com").isEmpty()) {
            final var librarian6 = new User("librarian6@mail.com", encoder.encode("password"), "Librarian");
            librarian6.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian6);
        }

        if (userRepo.findByUsername("librarian7@mail.com").isEmpty()) {
            final var librarian7 = new User("librarian7@mail.com", encoder.encode("password"), "Librarian");
            librarian7.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian7);
        }

        if (userRepo.findByUsername("librarian8@mail.com").isEmpty()) {
            final var librarian8 = new User("librarian8@mail.com", encoder.encode("password"), "Librarian");
            librarian8.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian8);
        }

        if (userRepo.findByUsername("librarian9@mail.com").isEmpty()) {
            final var librarian9 = new User("librarian9@mail.com", encoder.encode("password"), "Librarian");
            librarian9.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian9);
        }

        if (userRepo.findByUsername("librarian10@mail.com").isEmpty()) {
            final var librarian10 = new User("librarian10@mail.com", encoder.encode("password"), "Librarian");
            librarian10.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian10);
        }
    }
}


