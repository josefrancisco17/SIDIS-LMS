package libraryapi.bookservicecommand.rabbitMQ.Mapper;

import libraryapi.bookservicecommand.model.*;

import java.time.LocalDate;
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
        try {
            Pattern mainPattern = Pattern.compile(
                    "Author\\{" +
                            "id=(\\d+),\\s*" +
                            "name='(.*?)',\\s*" +
                            "shortBio='(.*?)',\\s*" +
                            "authorPhoto=(.+?)\\}"
            );

            Matcher mainMatcher = mainPattern.matcher(input);

            if (!mainMatcher.find()) {
                System.err.println("Failed to match main author pattern: " + input);
                return null;
            }

            Author author = new Author();
            author.setId(Long.parseLong(mainMatcher.group(1)));
            author.setName(mainMatcher.group(2));
            author.setShortBio(mainMatcher.group(3));

            String photoString = mainMatcher.group(4);

            if (!"null".equals(photoString)) {
                Pattern photoPattern = Pattern.compile(
                        "AuthorPhoto\\{" +
                                "id=(\\d+),\\s*" +
                                "author=null,\\s*" +
                                "image=\\[([^\\]]+)\\]"
                );

                Matcher photoMatcher = photoPattern.matcher(photoString);

                if (photoMatcher.find()) {
                    AuthorPhoto authorPhoto = new AuthorPhoto();
                    authorPhoto.setId(Long.parseLong(photoMatcher.group(1)));

                    String imageString = photoMatcher.group(2);
                    String[] imageValues = imageString.split(",\\s*");
                    byte[] imageData = new byte[imageValues.length];

                    for (int i = 0; i < imageValues.length; i++) {
                        try {
                            imageData[i] = Byte.parseByte(imageValues[i].trim());
                        } catch (NumberFormatException e) {
                            byte[] truncatedData = new byte[i];
                            System.arraycopy(imageData, 0, truncatedData, 0, i);
                            imageData = truncatedData;
                            break;
                        }
                    }

                    authorPhoto.setImage(imageData);
                    authorPhoto.setContentType("image/jpeg"); // Default content type
                    author.setAuthorPhoto(authorPhoto);
                } else {
                    System.err.println("Failed to match photo pattern but photo was present");
                }
            }

            return author;

        } catch (Exception e) {
            System.err.println("Error parsing author: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static List<Lending> StringToLendingList(String input) {
        List<Lending> lendings = new ArrayList<>();

        String[] lendingStrings = input.split("(?<=\\}),\\s*(?=Lending\\{)");

        for (String lendingString : lendingStrings) {
            lendingString = lendingString.trim();
            if (!lendingString.isEmpty()) {
                Lending lending = StringToLending(lendingString);
                if (lending != null) {
                    lendings.add(lending);
                }
            }
        }

        return lendings;
    }

    public static Lending StringToLending(String input) {
        Pattern pattern = Pattern.compile(
                "Lending\\{id=(null|\\d+),\\s*lendingCode='(.*?)',\\s*bookId=(\\d+),\\s*bookTitle='(.*?)',\\s*" +
                        "lendDate=(\\d{4}-\\d{2}-\\d{2}),\\s*limitDate=(\\d{4}-\\d{2}-\\d{2}),\\s*" +
                        "returnedDate=(null|\\d{4}-\\d{2}-\\d{2}),\\s*daysTillReturn=(\\d+),\\s*" +
                        "returned=(true|false),\\s*daysOverdue=(\\d+),\\s*fine=([\\d\\.]+),\\s*" +
                        "comment='(.*?)',\\s*readerId=(\\d+)\\}"
        );

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            Long id = "null".equals(matcher.group(1)) ? null : Long.parseLong(matcher.group(1));
            String lendingCode = matcher.group(2);
            Long bookId = Long.parseLong(matcher.group(3));
            String bookTitle = matcher.group(4);
            LocalDate lendDate = LocalDate.parse(matcher.group(5));
            LocalDate limitDate = LocalDate.parse(matcher.group(6));
            LocalDate returnedDate = "null".equals(matcher.group(7)) ? null : LocalDate.parse(matcher.group(7));
            int daysTillReturn = Integer.parseInt(matcher.group(8));
            boolean returned = Boolean.parseBoolean(matcher.group(9));
            int daysOverdue = Integer.parseInt(matcher.group(10));
            float fine = Float.parseFloat(matcher.group(11));
            String comment = matcher.group(12);
            Long readerId = Long.parseLong(matcher.group(13));

            Lending lending = new Lending();
            lending.setId(id);
            lending.setLendingCode(lendingCode);
            lending.setBookId(bookId);
            lending.setBookTitle(bookTitle);
            lending.setLendDate(lendDate);
            lending.setLimitDate(limitDate);
            lending.setReturnedDate(returnedDate);
            lending.setDaysTillReturn(daysTillReturn);
            lending.setReturned(returned);
            lending.setDaysOverdue(daysOverdue);
            lending.setFine(fine);
            lending.setComment(comment);
            lending.setReaderId(readerId);

            return lending;
        }

        return null;
    }

}
