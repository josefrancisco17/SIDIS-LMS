package libraryapi.authservicequery.rabbitMQ.consumer;

import libraryapi.authservicequery.model.Reader;
import libraryapi.authservicequery.model.Role;
import libraryapi.authservicequery.model.User;
import libraryapi.authservicequery.rabbitMQ.Mapper.RabbitMapper;
import libraryapi.authservicequery.repositories.UserRepository;
import libraryapi.authservicequery.services.UserService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class Receiver {
    @Autowired
    private Environment env;

    @Autowired
    private UserService userService;

    @RabbitListener(queues = "#{AuthSyncQueue.name}")
    public void receiveSyncAuth(Message message) {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        String senderInstancePort = (String) message.getMessageProperties().getHeaders().get("instancePort");

        if (currentPort.equals(senderInstancePort)) {
            System.out.println("[RabbitMQ] Ignored message from same instance: " + senderInstancePort);
            return;
        }
        String messageBody = new String(message.getBody());
        System.out.println("[RabbitMQ]  Auth sync: " + messageBody);
        User newUser = RabbitMapper.StringToUser(messageBody);
        userService.manageInternalUser(newUser);
    }

    @RabbitListener(queues = "#{ReaderSyncQueue.name}")
    public void receiveSyncReader(Message message) {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        String senderInstancePort = (String) message.getMessageProperties().getHeaders().get("instancePort");

        if (currentPort.equals(senderInstancePort)) {
            System.out.println("[RabbitMQ] Ignored message from same instance: " + senderInstancePort);
            return;
        }
        String messageBody = new String(message.getBody());
        System.out.println("[RabbitMQ]  Reader sync: " + messageBody);
        Reader newReader = RabbitMapper.StringToReader(messageBody);
        String email = newReader.getEmail() != null ? newReader.getEmail() : newReader.getId() + "@mail.com";
        String name = newReader.getName() != null ? newReader.getName() : "Reader" + newReader.getId();
        String password = "password";

        User newUser = User.newUser(email, password, name, Role.READER);
        userService.manageInternalUser(newUser);
    }
}
