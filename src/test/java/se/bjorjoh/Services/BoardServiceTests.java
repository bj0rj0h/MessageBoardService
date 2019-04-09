package se.bjorjoh.Services;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import se.bjorjoh.ErrorHandling.AuthenticationException;
import se.bjorjoh.ErrorHandling.JwtFormatException;
import se.bjorjoh.ErrorHandling.MissingMessageException;
import se.bjorjoh.ErrorHandling.UnauthorizedMessageAccessException;
import se.bjorjoh.models.Message;
import se.bjorjoh.repositories.HazelcastRepository;
import se.bjorjoh.services.BoardService;

import javax.validation.constraints.AssertFalse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ComponentScan("se.bjorjoh.services.BoardService")
public class BoardServiceTests {

    public static final String JWT_VALID = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoLmJqb3Jqb2guc2UiLCJpYXQiOjE1NTQ1NDU1OTQsImV4cCI6MTU4NjA4MTU5NCwiYXVkIjoibWVzc2FnZUJvYXJkLmJqb3Jqb2guc2UiLCJzdWIiOiJsaXNhLmVyaWtzc29uQGV4YW1wbGUuY29tIiwiR2l2ZW5OYW1lIjoiTGlzYSIsIlN1cm5hbWUiOiJFcmlrc3NvbiIsIkVtYWlsIjoibGlzYS5lcmlrc3NvbkBleGFtcGxlLmNvbSJ9.aH7qdF99fPraVD5WW6s3Om6Yl7xUOuIPV0tEj4AMFV4";
    public static final String JWT_INVALID_SIGNATURE = "yJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoLmJqb3Jqb2guc2UiLCJpYXQiOjE1NTQ1NDU1OTQsImV4cCI6MTU4NjA4MTU5NCwiYXVkIjoibWVzc2FnZUJvYXJkLmJqb3Jqb2guc2UiLCJzdWIiOiJsaXNhLmVyaWtzc29uQGV4YW1wbGUuY29tIiwiR2l2ZW5OYW1lIjoiTGlzYSIsIlN1cm5hbWUiOiJFcmlrc3NvbiIsIkVtYWlsIjoibGlzYS5lcmlrc3NvbkBleGFtcGxlLmNvbSJ9.aH7qdF99fPraVD5WW6s3Om6Yl7xUOuIPV0tEj4AMFV4";
    public static final String JWT_MISSING_EMAIL_CLAIM = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoLmJqb3Jqb2guc2UiLCJpYXQiOjE1NTQ1NDU1OTQsImV4cCI6MTU4NjA4MTU5NCwiYXVkIjoibWVzc2FnZUJvYXJkLmJqb3Jqb2guc2UiLCJzdWIiOiJsaXNhLmVyaWtzc29uQGV4YW1wbGUuY29tIiwiR2l2ZW5OYW1lIjoiTGlzYSIsIlN1cm5hbWUiOiJFcmlrc3NvbiJ9.u-o1TPP68ACcjcJ6w7SlFOIlGilxlkGTe3VglofeNtw";


    @TestConfiguration
    public static class BoardServiceTestContextConfiguration{

        @MockBean
        private HazelcastRepository hazelcastRepository;

        @Bean
        public BoardService boardService(){return new BoardService(hazelcastRepository);}

    }

    @Autowired
    private BoardService boardService;

    private Message message;
    ArrayList<Message> messageArrayList;




    @Before
    public void setUp(){
        message = new Message();
        message.setBody("Hello world");

        messageArrayList = new ArrayList<>();

        Message sample1 = new Message();
        sample1.setBody("Hello");
        sample1.setMessageId("abc123");
        sample1.setLastUpdated("someTime");
        sample1.setCreator("user@example.com");
        messageArrayList.add(sample1);

        Message sample2 = new Message();
        sample2.setBody("Hello");
        sample2.setMessageId("abc123");
        sample2.setLastUpdated("someTime");
        sample2.setCreator("user@example.com");
        messageArrayList.add(sample2);

    }



    @Test
    public void addMessage_validMessageValidJwt_messageReturned() throws IOException,AuthenticationException{

        Message result = boardService.addMessage(message, JWT_VALID);
        Assert.assertNotNull(result);


    }

    @Test(expected = AuthenticationException.class)
    public void addMessage_invalidJWTSignature_JWTVerificationExceptionThrown() throws IOException,AuthenticationException{

       boardService.addMessage(message, JWT_INVALID_SIGNATURE);

    }

    @Test(expected = IOException.class)
    public void addMessage_missingEmailClaimInJwt_IOExceptionThrown() throws IOException,AuthenticationException{

        boardService.addMessage(message, JWT_MISSING_EMAIL_CLAIM);

    }

    @Test
    public void getMessages_validRequest_messagesToBeReturned() {

        when(boardService.getBoardRepository().getMessages()).thenReturn(messageArrayList);

        List<Message> messageList = boardService.getAllMessages();
        assertTrue(messageList.size() >= 2);

    }

    @Test
    public void editMessage_validRequest_editToPass() throws JwtFormatException, AuthenticationException,
            UnauthorizedMessageAccessException, MissingMessageException {

        Message editedMessage = new Message();
        editedMessage.setBody("Hello");
        editedMessage.setMessageId("abc123");
        editedMessage.setLastUpdated("someTime");
        editedMessage.setCreator("lisa.eriksson@example.com");
        when(boardService.getBoardRepository().getMessage(anyString())).thenReturn(editedMessage);

        Message newMessage = new Message();
        newMessage.setBody("New hello");

        boardService.editMessage(message,"ABC123",JWT_VALID);

    }

    @Test(expected = AuthenticationException.class)
    public void editMessage_invalidJwt_JWTVerificationExceptionThrown() throws JwtFormatException,
            AuthenticationException, UnauthorizedMessageAccessException, MissingMessageException {

        boardService.editMessage(message,"ABC123",JWT_INVALID_SIGNATURE);

    }


    @Test(expected = UnauthorizedMessageAccessException.class)
    public void editMessage_unauthorizedAccess_UnauthorizedMessageAccessExceptionThrown() throws JwtFormatException,
            AuthenticationException, UnauthorizedMessageAccessException, MissingMessageException {

        Message editedMessage = new Message();
        editedMessage.setBody("Hello");
        editedMessage.setMessageId("abc123");
        editedMessage.setLastUpdated("someTime");
        editedMessage.setCreator("someone.else@example.com");
        when(boardService.getBoardRepository().getMessage(anyString())).thenReturn(editedMessage);

        Message newMessage = new Message();
        newMessage.setBody("New hello");

        boardService.editMessage(message,"ABC123",JWT_VALID);

    }

    @Test(expected = MissingMessageException.class)
    public void editMessage_nonExistingId_nullMessage() throws JwtFormatException,
            AuthenticationException, UnauthorizedMessageAccessException, MissingMessageException {

        when(boardService.getBoardRepository().getMessage(anyString())).thenReturn(null);

        Message newMessage = new Message();
        newMessage.setBody("New hello");

        boardService.editMessage(message,"ABC123",JWT_VALID);

    }


    @Test
    public void deleteMessage_validRequest_noExceptionThrown() throws JwtFormatException, AuthenticationException,
            UnauthorizedMessageAccessException, MissingMessageException {

        Message editedMessage = new Message();
        editedMessage.setBody("Hello");
        editedMessage.setMessageId("abc123");
        editedMessage.setLastUpdated("someTime");
        editedMessage.setCreator("lisa.eriksson@example.com");
        when(boardService.getBoardRepository().getMessage(anyString())).thenReturn(editedMessage);

        boardService.deleteMessage("ABC123",JWT_VALID);

    }

    @Test(expected = AuthenticationException.class)
    public void deleteMessage_invalidJwt_JWTVerificationExceptionThrown() throws JwtFormatException,
            AuthenticationException, UnauthorizedMessageAccessException, MissingMessageException {

        boardService.deleteMessage("ABC123",JWT_INVALID_SIGNATURE);

    }


    @Test(expected = UnauthorizedMessageAccessException.class)
    public void deleteMessage_unauthorizedAccess_UnauthorizedMessageAccessExceptionThrown() throws JwtFormatException,
            AuthenticationException, UnauthorizedMessageAccessException, MissingMessageException {

        Message editedMessage = new Message();
        editedMessage.setBody("Hello");
        editedMessage.setMessageId("abc123");
        editedMessage.setLastUpdated("someTime");
        editedMessage.setCreator("someone.else@example.com");
        when(boardService.getBoardRepository().getMessage(anyString())).thenReturn(editedMessage);


        boardService.deleteMessage("ABC123",JWT_VALID);

    }

    @Test(expected = MissingMessageException.class)
    public void deleteMessage_nonExistingId_nullMessage() throws JwtFormatException,
            AuthenticationException, UnauthorizedMessageAccessException, MissingMessageException {

        when(boardService.getBoardRepository().getMessage(anyString())).thenReturn(null);

        boardService.deleteMessage("ABC123",JWT_VALID);

    }


}
