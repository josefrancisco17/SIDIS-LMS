package libraryapi.authservice.bootstrapping;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import libraryapi.authservice.model.Role;
import libraryapi.authservice.model.User;
import libraryapi.authservice.repositories.UserRepository;

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

        if (userRepo.findByUsername("reader1@mail.com").isEmpty()) {
            final User reader1 = new User("reader1@mail.com", encoder.encode("password"),  "Reader");
            reader1.addAuthority(new Role(Role.READER));
            userRepo.save(reader1);
        }

        if (userRepo.findByUsername("reader2@mail.com").isEmpty()) {
            final User reader2 = new User("reader2@mail.com", encoder.encode("password"),  "Reader");
            reader2.addAuthority(new Role(Role.READER));
            userRepo.save(reader2);
        }

        if (userRepo.findByUsername("reader3@mail.com").isEmpty()) {
            final User reader3 = new User("reader3@mail.com", encoder.encode("password"),  "Reader");
            reader3.addAuthority(new Role(Role.READER));
            userRepo.save(reader3);
        }

        if (userRepo.findByUsername("reader4@mail.com").isEmpty()) {
            final User reader4 = new User("reader4@mail.com", encoder.encode("password"),  "Reader");
            reader4.addAuthority(new Role(Role.READER));
            userRepo.save(reader4);
        }

        if (userRepo.findByUsername("reader5@mail.com").isEmpty()) {
            final User reader5 = new User("reader5@mail.com", encoder.encode("password"),  "Reader");
            reader5.addAuthority(new Role(Role.READER));
            userRepo.save(reader5);
        }

        if (userRepo.findByUsername("reader6@mail.com").isEmpty()) {
            final User reader6 = new User("reader6@mail.com", encoder.encode("password"),  "Reader");
            reader6.addAuthority(new Role(Role.READER));
            userRepo.save(reader6);
        }

        if (userRepo.findByUsername("reader7@mail.com").isEmpty()) {
            final User reader7 = new User("reader7@mail.com", encoder.encode("password"),  "Reader");
            reader7.addAuthority(new Role(Role.READER));
            userRepo.save(reader7);
        }

        if (userRepo.findByUsername("reader8@mail.com").isEmpty()) {
            final User reader8 = new User("reader8@mail.com", encoder.encode("password"),  "Reader");
            reader8.addAuthority(new Role(Role.READER));
            userRepo.save(reader8);
        }

        if (userRepo.findByUsername("reader9@mail.com").isEmpty()) {
            final User reader9 = new User("reader9@mail.com", encoder.encode("password"),  "Reader");
            reader9.addAuthority(new Role(Role.READER));
            userRepo.save(reader9);
        }

        if (userRepo.findByUsername("reader10@mail.com").isEmpty()) {
            final User reader10 = new User("reader10@mail.com", encoder.encode("password"),  "Reader");
            reader10.addAuthority(new Role(Role.READER));
            userRepo.save(reader10);
        }

        if (userRepo.findByUsername("reader11@mail.com").isEmpty()) {
            final User reader11 = new User("reader11@mail.com", encoder.encode("password"),  "Reader");
            reader11.addAuthority(new Role(Role.READER));
            userRepo.save(reader11);
        }

        if (userRepo.findByUsername("reader12@mail.com").isEmpty()) {
            final User reader12 = new User("reader12@mail.com", encoder.encode("password"),  "Reader");
            reader12.addAuthority(new Role(Role.READER));
            userRepo.save(reader12);
        }

        if (userRepo.findByUsername("reader13@mail.com").isEmpty()) {
            final User reader13 = new User("reader13@mail.com", encoder.encode("password"),  "Reader");
            reader13.addAuthority(new Role(Role.READER));
            userRepo.save(reader13);
        }

        if (userRepo.findByUsername("reader14@mail.com").isEmpty()) {
            final User reader14 = new User("reader14@mail.com", encoder.encode("password"),  "Reader");
            reader14.addAuthority(new Role(Role.READER));
            userRepo.save(reader14);
        }

        if (userRepo.findByUsername("reader15@mail.com").isEmpty()) {
            final User reader15 = new User("reader15@mail.com", encoder.encode("password"),  "Reader");
            reader15.addAuthority(new Role(Role.READER));
            userRepo.save(reader15);
        }

        if (userRepo.findByUsername("reader16@mail.com").isEmpty()) {
            final User reader16 = new User("reader16@mail.com", encoder.encode("password"),  "Reader");
            reader16.addAuthority(new Role(Role.READER));
            userRepo.save(reader16);
        }

        if (userRepo.findByUsername("reader17@mail.com").isEmpty()) {
            final User reader17 = new User("reader17@mail.com", encoder.encode("password"),  "Reader");
            reader17.addAuthority(new Role(Role.READER));
            userRepo.save(reader17);
        }

        if (userRepo.findByUsername("reader18@mail.com").isEmpty()) {
            final User reader18 = new User("reader18@mail.com", encoder.encode("password"),  "Reader");
            reader18.addAuthority(new Role(Role.READER));
            userRepo.save(reader18);
        }

        if (userRepo.findByUsername("reader19@mail.com").isEmpty()) {
            final User reader19 = new User("reader19@mail.com", encoder.encode("password"),  "Reader");
            reader19.addAuthority(new Role(Role.READER));
            userRepo.save(reader19);
        }

        if (userRepo.findByUsername("reader20@mail.com").isEmpty()) {
            final User reader20 = new User("reader20@mail.com", encoder.encode("password"),  "Reader");
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


