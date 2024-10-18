package libraryapi.bookservice.services;

import jakarta.transaction.Transactional;
import libraryapi.bookservice.api.AuthorLentsViewMapper;
import libraryapi.bookservice.model.*;
import libraryapi.bookservice.repositories.BookRepository;
import libraryapi.bookservice.repositories.BookRepositoryHTTP;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import libraryapi.bookservice.repositories.AuthorPhotoRepository;
import libraryapi.bookservice.repositories.AuthorRepository;
import libraryapi.bookservice.exceptions.NotFoundException;
import libraryapi.bookservice.fileStorage.FileStorageService;
import libraryapi.bookservice.fileStorage.UploadFileResponse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static libraryapi.bookservice.util.AuthorUtil.isValidAuthorPhoto;

@Service
public class AuthorServiceImpl implements AuthorService{
    private final AuthorRepository authorRepository;
    private final AuthorLentsViewMapper authorLentsViewMapper;
    private final EditAuthorMapper editAuthorMapper;
    private final AuthorPhotoRepository authorPhotoRepository;
    private final FileStorageService fileStorageService;
    private final BookService bookService;
    private final BookRepository bookRepository;
    private BookRepositoryHTTP bookRepositoryHTTP;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository, EditAuthorMapper editAuthorMapper, AuthorLentsViewMapper authorLentsViewMapper, AuthorPhotoRepository authorPhotoRepository, FileStorageService fileStorageService, BookService bookService, BookRepository bookRepository, BookRepositoryHTTP bookRepositoryHTTP) {
        this.authorRepository = authorRepository;
        this.editAuthorMapper = editAuthorMapper;
        this.authorLentsViewMapper = authorLentsViewMapper;
        this.authorPhotoRepository = authorPhotoRepository;
        this.fileStorageService = fileStorageService;
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.bookRepositoryHTTP = bookRepositoryHTTP;
    }

    public int getTotalPages() {
        long totalAuthors = authorRepository.count();
        return (int) Math.ceil((double) totalAuthors / 5);
    }

    public Page<Author> getAuthors(Pageable pageable) {
        return authorRepository.findAll(pageable);
    }

    public Optional<Author> getAuthorsById(final Long id) {
        return authorRepository.findAuthorById(id);
    }

    public AuthorPhoto getAuthorPhoto(final String authorId) {
        final var existingAuthor = authorRepository.findById(Long.parseLong(authorId)).orElseThrow(() -> new NotFoundException("[ERROR] Author not found"));

        if (existingAuthor.getAuthorPhoto() == null) {
            throw new IllegalArgumentException("[ERROR] Author Photo not found with ID: " + existingAuthor.getId());
        }

        return existingAuthor.getAuthorPhoto();
    }

    public List<Author> getAuthorsByName(final String name) {
        return authorRepository.findAll()
                .stream()
                .filter(author -> author
                        .getName()
                        .toLowerCase()
                        .contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Book> getAuthorBooks(Long authorId) {
        List<BookAuthor> bookAuthors = bookService.getBookAuthorsByAuthorId(authorId);
        List<Book> booksList = new ArrayList<>();

        for (BookAuthor bookAuthor : bookAuthors) {
            booksList.add(bookAuthor.getBook());
        }
        return booksList;
    }

    public List<Author> getTop5Authors() {
        List<Lending> lendings = bookRepositoryHTTP.getAllLendings();

        List<Author> authors = authorRepository.findAll();
        for (Author author : authors) {
            List<BookAuthor> bookAuthors = bookService.getBookAuthorsByAuthorId(author.getId());
            int totalLents = 0;
            for (BookAuthor bookAuthor : bookAuthors) {
                for (Lending lending : lendings) {
                    if (lending.getBookId().equals(bookAuthor.getBook().getId())) {
                        totalLents++;
                    }
                }
            }
            author.setLents(totalLents);
        }
        authors.sort((a1, a2) -> Integer.compare(a2.getLents(), a1.getLents()));
        return authors.subList(0, Math.min(5, authors.size()));
    }


    public Author createAuthor(final EditAuthorRequest resource, MultipartFile authorPhoto) {
        validateCreateAuthorRequest(resource);

        Author author = editAuthorMapper.create(resource);

        authorRepository.save(author);

        if (authorPhoto != null) {
            doUploadFile(author.getId().toString(), authorPhoto);
        }

        return authorRepository.save(author);
    }
    @Transactional
    public Author updateAuthor(final Long id, final EditAuthorRequest resource, final long desiredVersion) {
        final var author = authorRepository.findById(id).orElseThrow(() -> new NotFoundException("[ERROR] Cannot update an object that does not yet exist"));
        validateCreateAuthorRequest(resource);
        author.updateData(desiredVersion, resource.getName(), resource.getShortBio());
        return authorRepository.save(author);
    }

    public Author partialUpdateAuthor(final Long id, final EditAuthorRequest resource, final long desiredVersion) {
        final var author = authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[ERROR] Cannot update an object that does not yet exist"));

        author.applyPatch(desiredVersion, resource.getName(), resource.getShortBio());

        return authorRepository.save(author);
    }

    public UploadFileResponse doUploadFile(final String id, final MultipartFile file) {
        System.out.println(isValidAuthorPhoto(file));
        if (isValidAuthorPhoto(file)) {

            AuthorPhoto authorPhoto = new AuthorPhoto();
            try {
                authorPhoto.setImage(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            authorPhoto.setContentType(file.getContentType());
            authorPhotoRepository.save(authorPhoto);
            Author author = authorRepository.getById(Long.parseLong(id));
            author.setAuthorPhoto(authorPhoto);
            authorRepository.save(author);
        }

        final String fileName = fileStorageService.storeFile(id, file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(fileName)
                .toUriString();

        fileDownloadUri = fileDownloadUri.replace("/photos/", "/photo/");

        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    public void validateCreateAuthorRequest(final EditAuthorRequest request) {
        if (StringUtils.isBlank(request.getName()) || StringUtils.isBlank(request.getShortBio())) {
            throw new IllegalArgumentException("[ERROR] Name and shortBio are mandatory.");
        }
    }
}