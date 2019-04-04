package se.bjorjoh.repositories;

import se.bjorjoh.models.Message;

import java.util.List;

public interface BoardRepository {

    List<Message> getMessages();
    Message getMessage(String messageId);
    void saveMessage(Message message);
    void editMessage(String messageId,Message message);
    void deleteMessage(String messageId);
}
