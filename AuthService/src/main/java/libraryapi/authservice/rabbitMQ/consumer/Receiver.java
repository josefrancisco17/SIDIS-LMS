package libraryapi.authservice.rabbitMQ.consumer;

import libraryapi.authservice.model.User;
import libraryapi.authservice.rabbitMQ.Mapper.RabbitMapper;
import libraryapi.authservice.repositories.UserRepository;
import libraryapi.authservice.services.UserService;
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

    @Autowired
    private UserRepository userRepository;

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

    @RabbitListener(queues = "#{AuthQueryQueue.name}")
    public String handleUsersRequest() {
        String users = userRepository.findAll().toString();
        System.out.println("[RabbitMQ] Sending users: " + users);
        return users;
    }
}
