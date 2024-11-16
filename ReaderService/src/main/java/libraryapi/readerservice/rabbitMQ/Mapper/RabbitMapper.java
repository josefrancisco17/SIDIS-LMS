package libraryapi.readerservice.rabbitMQ.Mapper;

import libraryapi.readerservice.model.Reader;
import libraryapi.readerservice.model.ReaderPhoto;

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

}
