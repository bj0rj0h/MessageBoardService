package se.bjorjoh.Services;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
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

        public HazelcastRepository getHazelcastRepository(){
            return hazelcastRepository;
        }

    }

    @Autowired
    private BoardService boardService;
    private Message message;



    @Before
    public void setUp(){
        message = new Message();
        message.setBody("Hello world");

    }

    @Test
    public void addMessage_validMessageValidJwt_messageReturned() throws IOException{

        Message result = boardService.addMessage(message, JWT_VALID);
        Assert.assertNotNull(result);


    }

    @Test(expected = JWTVerificationException.class)
    public void addMessage_invalidJWTSignature_JWTVerificationExceptionThrown() throws IOException{

       boardService.addMessage(message, JWT_INVALID_SIGNATURE);

    }

    @Test(expected = IOException.class)
    public void addMessage_missingEmailClaimInJwt_IOExceptionThrown() throws IOException{

        boardService.addMessage(message, JWT_MISSING_EMAIL_CLAIM);

    }

    @Test
    public void getMessages_validRequest_messagesToBeReturned() {

        ArrayList<Message> messageArrayList = new ArrayList<>();

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

        when(boardService.getBoardRepository().getMessages()).thenReturn(messageArrayList);

        List<Message> messageList = boardService.getAllMessages();
        assertTrue(messageList.size() == 2);

    }

}
