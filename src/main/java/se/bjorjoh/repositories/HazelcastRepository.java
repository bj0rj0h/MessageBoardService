package se.bjorjoh.repositories;

import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import se.bjorjoh.models.Message;
import se.bjorjoh.services.BoardService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class HazelcastRepository implements BoardRepository {

    Logger logger = LoggerFactory.getLogger(BoardService.class);
    private static final String MESSAGE_MAP_NAME = "se.bjorjoh.messages";
    private HazelcastInstance hazelcastInstance;

    public HazelcastRepository(HazelcastInstance hazelcastInstance){
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public List<Message> getMessages() {

        Map messages = hazelcastInstance.getMap(MESSAGE_MAP_NAME);

        List<Message> messageList = new ArrayList<>();

        messages.forEach((key, value) -> {
            messageList.add((Message)value);
        });

        logger.info("Returning " + messageList.size() + " messages");

        return messageList;
    }

    @Override
    public Message getMessage(String messageId) {
        Map<String,Message> messages = hazelcastInstance.getMap(MESSAGE_MAP_NAME);
        Message result = messages.get(messageId);
        logger.info("Returning message with id: " + messageId);
        return result;
    }

    @Override
    public String saveMessage(Message message) {

        UUID uuid = UUID.randomUUID();
        Map<String,Message> messages = hazelcastInstance.getMap(MESSAGE_MAP_NAME);
        String uuidAsString = uuid.toString();
        message.setMessageId(uuidAsString);
        messages.put(uuidAsString,message);
        logger.info("Message added with id " + uuidAsString);
        return uuidAsString;
    }

    @Override
    public void editMessage(String messageId, Message message) {
        Map<String,Message> messages = hazelcastInstance.getMap(MESSAGE_MAP_NAME);
        messages.put(messageId,message);
        logger.info("Message with id " +messageId + " edited");

    }

    @Override
    public Message deleteMessage(String messageId) {
        Map<String,Message> messages = hazelcastInstance.getMap(MESSAGE_MAP_NAME);
        return messages.remove(messageId);
    }
}
