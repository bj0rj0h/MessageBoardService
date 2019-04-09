package se.bjorjoh.services;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.bjorjoh.ErrorHandling.AuthenticationException;
import se.bjorjoh.ErrorHandling.JwtFormatException;
import se.bjorjoh.ErrorHandling.MissingMessageException;
import se.bjorjoh.ErrorHandling.UnauthorizedMessageAccessException;
import se.bjorjoh.models.JwtContent;
import se.bjorjoh.models.Message;
import se.bjorjoh.repositories.BoardRepository;
import se.bjorjoh.repositories.HazelcastRepository;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BoardService implements MessageService {

    private final BoardRepository boardRepository;
    Logger logger = LoggerFactory.getLogger(BoardService.class);

    @Override
    public BoardRepository getBoardRepository(){
        return boardRepository;
    }

    @Autowired
    public BoardService (HazelcastRepository hazelcastRepository){
        boardRepository = hazelcastRepository;
    }

    private String getCurrentTimeAsISO8601(){
        TimeZone tz = TimeZone.getDefault();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        return nowAsISO;
    }

    private JwtContent getJwtContent(String jwtPayload) throws IOException{
        byte[] decodedBytes = Base64.getDecoder().decode(jwtPayload);
        String plainTextJwt = new String(decodedBytes,"UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        try {
            JwtContent content = mapper.readValue(plainTextJwt,JwtContent.class);
            return content;
        } catch (IOException e) {
            logger.error("Error while decoding jwt payload",e);
            throw e;
        }
    }

    @Override
    public Message addMessage(Message message, String jwtString) throws IOException,AuthenticationException {

        try {
            DecodedJWT jwt = JwtAuthorizer.getAndVerifyJWT(jwtString);
            JwtContent jwtContent = getJwtContent(jwt.getPayload());
            addMessageForUser(jwtContent,message);
        } catch (JWTVerificationException e){
            logger.error("Error while validating jwt signature",e);
            throw new AuthenticationException();
        } catch (IOException e){
            throw e;
        }
        return message;

    }

    private Message addMessageForUser(JwtContent jwtContent,Message message) {

        String userEmail = jwtContent.getEmail();
        message.setCreator(userEmail);

        String nowAsISO=getCurrentTimeAsISO8601();
        message.setLastUpdated(nowAsISO);

        String uuid = boardRepository.saveMessage(message);
        message.setMessageId(uuid);

        return message;

    }

    @Override
    public List<Message> getAllMessages(){

        return boardRepository.getMessages();
    }

    private void editAndDeletePreValidation(Message message,JwtContent jwtContent,String messageId) throws MissingMessageException,
            UnauthorizedMessageAccessException{

        if (message == null){
            logger.warn("No message found with id: " + messageId);
            throw new MissingMessageException("No message with the given ID exists");
        }

        if (!(message.getCreator().equals(jwtContent.getEmail()))){
            throw new UnauthorizedMessageAccessException();
        }
    }

    @Override
    public Message editMessage(Message message, String messageId, String jwtString)
            throws JwtFormatException, AuthenticationException, UnauthorizedMessageAccessException, MissingMessageException {

        JwtContent jwtContent;

        try {
            DecodedJWT jwt = JwtAuthorizer.getAndVerifyJWT(jwtString);
            jwtContent = getJwtContent(jwt.getPayload());
        } catch (JWTVerificationException e){
            logger.error("Error while validating jwt signature",e);
            throw new AuthenticationException();
        } catch (IOException e){
            logger.error("Error while parsing JWT");
            throw new JwtFormatException();
        }
        Message editedMessage = editMessageForUser(jwtContent,message,messageId);
        return editedMessage;
    }

    private Message editMessageForUser(JwtContent jwtContent, Message message,String messageId)
            throws UnauthorizedMessageAccessException,MissingMessageException {

        Message storedMessage = boardRepository.getMessage(messageId);

        editAndDeletePreValidation(storedMessage,jwtContent,messageId);

        String nowAsISO=getCurrentTimeAsISO8601();
        storedMessage.setLastUpdated(nowAsISO);
        storedMessage.setBody(message.getBody());

        boardRepository.editMessage(messageId,storedMessage);

        return storedMessage;
    }

    @Override
    public void deleteMessage(String messageId, String jwtString) throws AuthenticationException, JwtFormatException, UnauthorizedMessageAccessException, MissingMessageException {

        JwtContent jwtContent;

        try {
            DecodedJWT jwt = JwtAuthorizer.getAndVerifyJWT(jwtString);
            jwtContent = getJwtContent(jwt.getPayload());
        } catch (JWTVerificationException e){
            logger.error("Error while validating jwt signature",e);
            throw new AuthenticationException();
        } catch (IOException e){
            logger.error("Error while parsing JWT");
            throw new JwtFormatException();
        }

        deleteMessageForUser(jwtContent,messageId);

    }

    private Message deleteMessageForUser(JwtContent jwtContent,String messageId)
            throws UnauthorizedMessageAccessException,MissingMessageException {

        Message storedMessage = boardRepository.getMessage(messageId);
        editAndDeletePreValidation(storedMessage,jwtContent,messageId);

        boardRepository.deleteMessage(messageId);

        return storedMessage;
    }
}
