package se.bjorjoh.repositories;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.stereotype.Repository;
import se.bjorjoh.models.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class HazelcastRepository implements BoardRepository {

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

        return messageList;
    }

    @Override
    public Message getMessage(String messageId) {
        Map<String,Message> messages = hazelcastInstance.getMap(MESSAGE_MAP_NAME);
        Message result = messages.get(messageId);
        return result;
    }

    @Override
    public String saveMessage(Message message) {

        UUID uuid = UUID.randomUUID();
        Map<String,Message> messages = hazelcastInstance.getMap(MESSAGE_MAP_NAME);
        String uuidAsString = uuid.toString();
        message.setMessageId(uuidAsString);
        messages.put(uuidAsString,message);
        return uuidAsString;
    }

    @Override
    public void editMessage(String messageId, Message message) {
        Map<String,Message> messages = hazelcastInstance.getMap(MESSAGE_MAP_NAME);
        messages.put(messageId,message);

    }

    @Override
    public Message deleteMessage(String messageId) {
        Map<String,Message> messages = hazelcastInstance.getMap(MESSAGE_MAP_NAME);
        return messages.remove(messageId);
    }
}
