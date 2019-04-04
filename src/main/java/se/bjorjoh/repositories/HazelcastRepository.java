package se.bjorjoh.repositories;

import org.springframework.stereotype.Repository;
import se.bjorjoh.models.Message;

import java.util.List;

@Repository
public class HazelcastRepository implements BoardRepository {
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

    }

    @Override
    public void editMessage(String messageId, Message message) {

    }

    @Override
    public void deleteMessage(String messageId) {

    }
}
