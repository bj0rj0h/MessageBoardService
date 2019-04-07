package se.bjorjoh.repositories;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.stereotype.Repository;
import se.bjorjoh.models.Message;

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
        return null;
    }

    @Override
    public Message getMessage(String messageId) {
        return null;
    }

    @Override
    public void saveMessage(Message message) {

        UUID uuid = UUID.randomUUID();
        Map<String,Message> messages = hazelcastInstance.getMap(MESSAGE_MAP_NAME);
        messages.put(uuid.toString(),message);
    }

    @Override
    public void editMessage(String messageId, Message message) {

    }

    @Override
    public void deleteMessage(String messageId) {

    }
}
