package libraryapi.authservicecommand.rabbitMQ.Mapper;

import libraryapi.authservicecommand.model.Role;
import libraryapi.authservicecommand.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
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
}
