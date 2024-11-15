package libraryapi.readerservice.rabbitMQ.Mapper;

import libraryapi.readerservice.model.Reader;

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
                        "name='(.*?)',\\s*email='(.*?)',\\s*dateOfBirth=(\\d{4}-\\d{2}-\\d{2}),\\s*" +
                        "age=(null|\\d+),\\s*phoneNumber=(\\d+),\\s*GDBRConsent=(true|false),\\s*interests=(null|.*?)\\}"
        );

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            Long id = Long.parseLong(matcher.group(1));
            String readerCode = matcher.group(2);
            long version = Long.parseLong(matcher.group(3));
            String name = matcher.group(4);
            String email = matcher.group(5);
            LocalDate dateOfBirth = LocalDate.parse(matcher.group(6));
            Integer age = Period.between(dateOfBirth, LocalDate.now()).getYears();
            Integer phoneNumber = Integer.parseInt(matcher.group(8));
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

            Reader reader = new Reader(readerCode, name, email, dateOfBirth, phoneNumber, GDBRConsent, interests);
            reader.setId(id);
            reader.setVersion(version);
            reader.setAge(age);
            return reader;
        }

        return null;
    }
}
