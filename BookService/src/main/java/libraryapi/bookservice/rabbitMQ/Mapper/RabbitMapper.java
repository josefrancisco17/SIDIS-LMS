package libraryapi.bookservice.rabbitMQ.Mapper;

import libraryapi.bookservice.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RabbitMapper {
    public static Book StringToBook(String input) {
        Pattern pattern = Pattern.compile(
                "Book\\{" +
                        "id=(\\d+),\\s*" +
                        "isbn='(.*?)',\\s*" +
                        "title='(.*?)',\\s*" +
                        "genre='(null|Genre\\{id=(\\d+),\\s*name='(.*?)'\\})',\\s*" +
                        "description='(null|.*?)',\\s*" +
                        "authors=\\[(.*?)\\],\\s*" +
                        "cover=(null|BookCover\\{id=(\\d+),\\s*book=null,\\s*" +
                        "image=\\[(.*?)\\],\\s*contentType='(.*?)'\\})\\s*" +
                        "\\}"
        );

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            try {
                Long id = Long.parseLong(matcher.group(1));
                String isbn = matcher.group(2);
                String title = matcher.group(3);

                Genre genre = null;
                String genreString = matcher.group(4);
                if (!"null".equals(genreString)) {
                    Long genreId = Long.parseLong(matcher.group(5));
                    String genreName = matcher.group(6);
                    genre = new Genre(genreId, genreName);
                }

                String description = "null".equals(matcher.group(7)) ? null : matcher.group(7);

                List<Author> authors = new ArrayList<>();
                String authorsString = matcher.group(8);
                if (!authorsString.isEmpty()) {
                    Pattern authorPattern = Pattern.compile(
                            "Author\\{name='(.*?)',\\s*shortBio='(.*?)'\\}"
                    );
                    Matcher authorMatcher = authorPattern.matcher(authorsString);
                    while (authorMatcher.find()) {
                        String authorName = authorMatcher.group(1);
                        String shortBio = authorMatcher.group(2);
                        Author author = new Author(authorName, shortBio);
                        authors.add(author);
                    }
                }

                BookCover bookCover = null;
                if (!"null".equals(matcher.group(9))) {
                    Long coverId = Long.parseLong(matcher.group(10));
                    String imageString = matcher.group(11);
                    String contentType = matcher.group(12);

                    byte[] image = null;
                    if (!imageString.isEmpty()) {
                        String[] imageArray = imageString.split(",");
                        image = new byte[imageArray.length];
                        for (int i = 0; i < imageArray.length; i++) {
                            image[i] = Byte.parseByte(imageArray[i].trim());
                        }
                    }

                    bookCover = new BookCover();
                    bookCover.setId(coverId);
                    bookCover.setImage(image);
                    bookCover.setContentType(contentType);
                }

                Book book = new Book(isbn, title, genre, description);
                book.setId(id);

                if (!authors.isEmpty()) {
                    book.setAuthors(authors);
                }

                if (bookCover != null) {
                    book.setCover(bookCover);
                }
                return book;

            } catch (IllegalArgumentException e) {
                System.err.println("Error parsing book string: " + e.getMessage());
                return null;
            }
        }

        return null;
    }

    public static Author StringToAuthor(String input) {
        Pattern pattern = Pattern.compile(
                "Author\\{" +
                        "name='(.*?)',\\s*" +
                        "shortBio='(.*?)',\\s*" +
                        "authorPhoto=(null|AuthorPhoto\\{id=(\\d+),\\s*author=null,\\s*" +
                        "image=\\[(.*?)\\],\\s*contentType='(.*?)'\\})\\s*" +
                        "\\}"
        );

        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            try {
                // Extract basic author information
                String name = matcher.group(1);
                String shortBio = matcher.group(2);

                Author author = new Author(name, shortBio);

                // Handle AuthorPhoto if present
                String photoString = matcher.group(3);
                if (!"null".equals(photoString)) {
                    Long photoId = Long.parseLong(matcher.group(4));
                    String imageString = matcher.group(5);
                    String contentType = matcher.group(6);

                    // Parse image byte array
                    byte[] image = null;
                    if (!imageString.isEmpty()) {
                        String[] imageArray = imageString.split(",");
                        image = new byte[imageArray.length];
                        for (int i = 0; i < imageArray.length; i++) {
                            image[i] = Byte.parseByte(imageArray[i].trim());
                        }
                    }

                    // Create a new AuthorPhoto instance
                    AuthorPhoto authorPhoto = new AuthorPhoto();
                    authorPhoto.setId(photoId); // Optional: Avoid setting ID for new entities
                    authorPhoto.setImage(image);
                    authorPhoto.setContentType(contentType);

                    // Associate with Author
                    author.setAuthorPhoto(authorPhoto);
                }

                return author;

            } catch (IllegalArgumentException e) {
                System.err.println("Error parsing author string: " + e.getMessage());
                return null;
            }
        }

        return null;
    }

}
