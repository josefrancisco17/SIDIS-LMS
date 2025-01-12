package libraryapi.recommendationservicecommand.rabbitMQ.consumer;

import jakarta.persistence.EntityNotFoundException;
import libraryapi.recommendationservicecommand.model.*;
import libraryapi.recommendationservicecommand.rabbitMQ.Mapper.RabbitMapper;
import libraryapi.recommendationservicecommand.rabbitMQ.producer.Sender;
import libraryapi.recommendationservicecommand.repositories.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;

import java.util.Objects;
import java.util.Optional;

@Component
public class Receiver {
    @Autowired
    private Environment env;

    @Autowired
    private TempLendingRepository tempLendingRepository;

    @Autowired
    private LendingRepository lendingRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private Sender sender;

    @RabbitListener(queues = "#{TempLendingSyncQueue.name}")
    public void receiveSyncTempLending(Message message) {
        String messageBody = new String(message.getBody());
        System.out.println("[RabbitMQ]  TempLending sync: " + messageBody);
        TempLending tempLending = RabbitMapper.StringToTempLending(messageBody);
        System.out.println("[RabbitMQ]  TempLending : " + tempLending);
        Recommendation recommendation = new Recommendation();
        recommendation.setLendingCode(tempLending.getLendingCode());
        recommendation.setRecommended(tempLending.getRecommended());
        sender.sendSyncRecommendation(recommendation, (String) message.getMessageProperties().getHeaders().get("instancePort"));
    }


    @RabbitListener(queues = "#{RecommendationSyncQueue.name}")
    public void receiveSyncRecommendation(Message message) {
        String messageBody = new String(message.getBody());
        System.out.println("[RabbitMQ]  Recommendation sync: " + messageBody);
        Recommendation recommendation = RabbitMapper.StringToRecommendation(messageBody);

        if (recommendationRepository.findByLendingCode(recommendation.getLendingCode()).isEmpty()) {
            recommendationRepository.save(recommendation);
        } else {
            System.out.println("[RabbitMQ]  Recommendation already exists: " + recommendation.getId());
        }
    }

    @RabbitListener(queues = "#{LendingSyncQueue.name}")
    public void receiveSyncLending(Message message) {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        String senderInstancePort = (String) message.getMessageProperties().getHeaders().get("instancePort");

        if (currentPort.equals(senderInstancePort)) {
            System.out.println("[RabbitMQ] Ignored message from same instance: " + senderInstancePort);
            return;
        }
        String messageBody = new String(message.getBody());
        System.out.println("[RabbitMQ]  Lending sync: " + messageBody);
        Lending newLending = RabbitMapper.StringToLending(messageBody);
        lendingRepository.save(newLending);
    }
}
