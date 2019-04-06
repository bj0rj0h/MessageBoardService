package se.bjorjoh;

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

@RunWith(SpringRunner.class)
@ComponentScan("se.bjorjoh.services.BoardService")
public class BoardServiceTests {

    private static final String SAMPLE_JWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoLmJqb3Jqb2guc2UiLCJpYXQiOjE1NTQ1NDU1OTQsImV4cCI6MTU4NjA4MTU5NCwiYXVkIjoibWVzc2FnZUJvYXJkLmJqb3Jqb2guc2UiLCJzdWIiOiJsaXNhLmVyaWtzc29uQGV4YW1wbGUuY29tIiwiR2l2ZW5OYW1lIjoiTGlzYSIsIlN1cm5hbWUiOiJFcmlrc3NvbiIsIkVtYWlsIjoibGlzYS5lcmlrc3NvbkBleGFtcGxlLmNvbSJ9.aH7qdF99fPraVD5WW6s3Om6Yl7xUOuIPV0tEj4AMFV4";


    @TestConfiguration
    static class BoardServiceTestContextConfiguration{

        @MockBean
        HazelcastRepository hazelcastRepository;

        @Bean
        public BoardService boardService(){return new BoardService(hazelcastRepository);}
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
    public void addMessage_validMessage_messageReturned(){

        Message result = boardService.addMessage(message,SAMPLE_JWT);
        Assert.assertNotNull(result);


    }
}
