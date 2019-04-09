package se.bjorjoh.services;

import se.bjorjoh.ErrorHandling.AuthenticationException;
import se.bjorjoh.ErrorHandling.JwtFormatException;
import se.bjorjoh.ErrorHandling.MissingMessageException;
import se.bjorjoh.ErrorHandling.UnauthorizedMessageAccessException;
import se.bjorjoh.models.Message;
import se.bjorjoh.repositories.BoardRepository;

import java.io.IOException;
import java.util.List;

public interface MessageService {
    BoardRepository getBoardRepository();

    Message addMessage(Message message, String jwtString) throws IOException, AuthenticationException;

    List<Message> getAllMessages();

    Message editMessage(Message message, String messageId, String jwtString)
            throws JwtFormatException, AuthenticationException, UnauthorizedMessageAccessException, MissingMessageException;

    void deleteMessage(String messageId, String jwtString) throws AuthenticationException, JwtFormatException, UnauthorizedMessageAccessException, MissingMessageException;
}
