package libraryapi.readerservice.rabbitMQ.Mapper;

import libraryapi.readerservice.model.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RabbitMapper {
    public static Reader StringToReader(String input) {
        Pattern pattern = Pattern.compile(
                "Reader\\{id=(\\d+),\\s*readerCode='(.*?)',\\s*version=(\\d+),\\s*" +
                        "name='(.*?)',\\s*email='(.*?)',\\s*dateOfBirth=(null|\\d{4}-\\d{2}-\\d{2}),\\s*" +
                        "age=(null|\\d+),\\s*phoneNumber=(null|\\d+),\\s*GDBRConsent=(true|false),\\s*" +
                        "interests=(null|.*?),\\s*ReaderPhoto=(null|ReaderPhoto\\{id=(\\d+),\\s*reader=null,\\s*" +
                        "image=\\[(.*?)\\],\\s*contentType='(.*?)'\\})\\}"
        );

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            Long id = Long.parseLong(matcher.group(1));
            String readerCode = matcher.group(2);
            long version = Long.parseLong(matcher.group(3));
            String name = matcher.group(4);
            String email = matcher.group(5);

            LocalDate dateOfBirth = "null".equals(matcher.group(6)) ? null : LocalDate.parse(matcher.group(6));
            Integer age = "null".equals(matcher.group(7)) || dateOfBirth == null
                    ? null
                    : Period.between(dateOfBirth, LocalDate.now()).getYears();
            Integer phoneNumber = "null".equals(matcher.group(8)) ? null : Integer.parseInt(matcher.group(8));
            Boolean GDBRConsent = Boolean.parseBoolean(matcher.group(9));
            String interestsString = matcher.group(10);

            List<String> interests = null;
            if (!"null".equals(interestsString)) {
                interests = new ArrayList<>();
                String[] interestsArray = interestsString.split(",");
                for (String interest : interestsArray) {
                    interests.add(interest.trim());
                }
            }

            ReaderPhoto readerPhoto = null;
            if (!"null".equals(matcher.group(11))) {
                Long photoId = Long.parseLong(matcher.group(12));
                String imageString = matcher.group(13);
                String contentType = matcher.group(14);

                byte[] image = null;
                if (!imageString.isEmpty()) {
                    String[] imageArray = imageString.split(",");
                    image = new byte[imageArray.length];
                    for (int i = 0; i < imageArray.length; i++) {
                        image[i] = Byte.parseByte(imageArray[i].trim());
                    }
                }

                readerPhoto = new ReaderPhoto();
                readerPhoto.setId(photoId);
                readerPhoto.setImage(image);
                readerPhoto.setContentType(contentType);
            }

            Reader reader = new Reader(readerCode, name, email, dateOfBirth, phoneNumber, GDBRConsent, interests);
            reader.setId(id);
            reader.setVersion(version);
            reader.setAge(age);
            reader.setReaderPhoto(readerPhoto);

            return reader;
        }

        return null;
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

    public static List<Book> stringToBookList(String input) {
        List<Book> books = new ArrayList<>();

        input = input.trim();
        if (input.startsWith("[") && input.endsWith("]")) {
            input = input.substring(1, input.length() - 1).trim();
        }
        String[] bookStrings = input.split("(?<=\\}),\\s*(?=Book\\{)");

        for (String bookString : bookStrings) {
            bookString = bookString.trim();
            if (!bookString.isEmpty()) {
                Book book = StringToBook(bookString);
                if (book != null) {
                    books.add(book);
                }
            }
        }

        return books;
    }

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

}
