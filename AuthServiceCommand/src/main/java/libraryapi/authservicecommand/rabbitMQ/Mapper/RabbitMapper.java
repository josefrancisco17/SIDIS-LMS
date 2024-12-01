package libraryapi.authservicecommand.rabbitMQ.Mapper;

import libraryapi.authservicecommand.model.Reader;
import libraryapi.authservicecommand.model.ReaderPhoto;
import libraryapi.authservicecommand.model.Role;
import libraryapi.authservicecommand.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RabbitMapper {
    public static User StringToUser(String input) {
        Pattern pattern = Pattern.compile(
                "User\\{username='(.*?)',\\s*password='(.*?)',\\s*authorities=\\[(.*?)\\],\\s*" +
                        "id=(\\d+),\\s*version=(\\d+),\\s*createdAt=(.*?),\\s*createdBy='(.*?)',\\s*" +
                        "modifiedAt=(.*?),\\s*modifiedBy='(.*?)',\\s*enabled=(true|false),\\s*" +
                        "fullName='(.*?)'\\}"
        );

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            // Extract fields from the pattern
            String username = matcher.group(1);
            String password = matcher.group(2);

            // Parse authorities (roles)
            String authoritiesString = matcher.group(3);
            Set<Role> authorities = new HashSet<>();
            if (!"null".equals(authoritiesString)) {
                String[] rolesArray = authoritiesString.split(",");
                for (String role : rolesArray) {
                    authorities.add(new Role(role.trim()));
                }
            }

            Long id = Long.parseLong(matcher.group(4));
            Long version = Long.parseLong(matcher.group(5));

            // Parse date fields
            LocalDateTime createdAt = LocalDateTime.parse(matcher.group(6));
            String createdBy = matcher.group(7);
            LocalDateTime modifiedAt = LocalDateTime.parse(matcher.group(8));
            String modifiedBy = matcher.group(9);

            boolean enabled = Boolean.parseBoolean(matcher.group(10));
            String fullName = matcher.group(11);

            // Create and return the User object
            User user = new User(username, password, authorities);
            user.setId(id);
            user.setVersion(version);
            user.setCreatedAt(createdAt);
            user.setCreatedBy(createdBy);
            user.setModifiedAt(modifiedAt);
            user.setModifiedBy(modifiedBy);
            user.setEnabled(enabled);
            user.setFullName(fullName);

            return user;
        }

        return null;
    }

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
}
