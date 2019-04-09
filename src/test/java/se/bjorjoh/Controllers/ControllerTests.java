package se.bjorjoh.Controllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import se.bjorjoh.ErrorHandling.AuthenticationException;
import se.bjorjoh.ErrorHandling.JwtFormatException;
import se.bjorjoh.ErrorHandling.MissingMessageException;
import se.bjorjoh.ErrorHandling.UnauthorizedMessageAccessException;
import se.bjorjoh.Services.BoardServiceTests;
import se.bjorjoh.models.ErrorModel;
import se.bjorjoh.models.Message;
import se.bjorjoh.services.BoardService;
import se.bjorjoh.services.MessageService;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ComponentScan("se.bjorjoh.controllers")
@ComponentScan("se.bjorjoh.services")
@TestPropertySource(locations = "/application.properties")
public class ControllerTests {

    private static final String AUTHORIZATION_MISSING_BEARER_PREFIX = "abc123";


    @MockBean
    private MessageService boardService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    private String testHost;

    @Before
    public void setUp(){
        this.testHost = "http://localhost:" + port;
    }

    @Test
    public void addMessage_validMessage_status201(){

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION,"Bearer " + BoardServiceTests.JWT_VALID);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        Message message = new Message();
        message.setBody("Hello");
        HttpEntity<Message> messageHttpEntity = new HttpEntity<>(message,headers);
        ResponseEntity<Message> response = this.template.exchange(testHost + "/messages", HttpMethod.POST,messageHttpEntity,Message.class);
        assertThat(response.getStatusCode(),is(HttpStatus.CREATED));

    }

    @Test
    public void addMessage_invalidJwtSignature_status401() throws IOException,AuthenticationException {

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION,"Bearer " + BoardServiceTests.JWT_INVALID_SIGNATURE);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        when(boardService.addMessage(any(Message.class),anyString())).thenThrow(AuthenticationException.class);

        Message message = new Message();
        message.setBody("Hello");
        HttpEntity<Message> messageHttpEntity = new HttpEntity<>(message,headers);
        ResponseEntity<Message> response = this.template.exchange("http://localhost:" + port+ "/messages", HttpMethod.POST,messageHttpEntity,Message.class);
        assertThat(response.getStatusCode(),is(HttpStatus.UNAUTHORIZED));

    }

    @Test
    public void addMessage_authorizationHeaderMissingBearerPrefix_status401() {

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION,this.AUTHORIZATION_MISSING_BEARER_PREFIX);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        Message message = new Message();
        message.setBody("Hello");
        HttpEntity<Message> messageHttpEntity = new HttpEntity<>(message,headers);
        ResponseEntity<Message> response = this.template.exchange("http://localhost:" + port+ "/messages", HttpMethod.POST,messageHttpEntity,Message.class);
        assertThat(response.getStatusCode(),is(HttpStatus.UNAUTHORIZED));

    }

    @Test
    public void addMessage_authorizationHeaderMissingJwtValue_status401() {

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION,"Bearer");
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        Message message = new Message();
        message.setBody("Hello");
        HttpEntity<Message> messageHttpEntity = new HttpEntity<>(message,headers);
        ResponseEntity<Message> response = this.template.exchange("http://localhost:" + port+ "/messages", HttpMethod.POST,messageHttpEntity,Message.class);
        assertThat(response.getStatusCode(),is(HttpStatus.UNAUTHORIZED));

    }

    @Test
    public void addMessage_authorizationHeaderMissing_status401() {

        HttpHeaders headers = new HttpHeaders();

        Message message = new Message();
        message.setBody("Hello");
        HttpEntity<Message> messageHttpEntity = new HttpEntity<>(message,headers);
        ResponseEntity<Message> response = this.template.exchange("http://localhost:" + port+ "/messages", HttpMethod.POST,messageHttpEntity,Message.class);
        assertThat(response.getStatusCode(),is(HttpStatus.UNAUTHORIZED));

    }

    @Test
    public void getMessages_validRequest_status200() {
        
        ResponseEntity<List<Message>> response = this.template.exchange("http://localhost:" + port+ "/messages", HttpMethod.GET,null,new ParameterizedTypeReference<List<Message>>(){});
        assertThat(response.getStatusCode(),is(HttpStatus.OK));

    }

    @Test
    public void editMessage_validRequest_status200() {

        String messageId = "abc123";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION,"Bearer " + BoardServiceTests.JWT_VALID);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        Message message = new Message();
        message.setBody("Hello");
        HttpEntity<Message> messageHttpEntity = new HttpEntity<>(message,headers);

        ResponseEntity<Message> response = this.template.exchange("http://localhost:" + port+ "/messages/"+messageId, HttpMethod.PUT,messageHttpEntity,Message.class);
        assertThat(response.getStatusCode(),is(HttpStatus.OK));

    }

    @Test
    public void editMessage_unauthorizedAccess_status403() throws JwtFormatException, AuthenticationException, UnauthorizedMessageAccessException, MissingMessageException {

        String messageId = "abc123";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION,"Bearer " + BoardServiceTests.JWT_VALID);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        Message message = new Message();
        message.setBody("Hello");
        HttpEntity<Message> messageHttpEntity = new HttpEntity<>(message,headers);
        when(boardService.editMessage(any(Message.class),anyString(),anyString())).thenThrow(UnauthorizedMessageAccessException.class);


        ResponseEntity<Message> response = this.template.exchange("http://localhost:" + port+ "/messages/"+messageId, HttpMethod.PUT,messageHttpEntity,Message.class);
        assertThat(response.getStatusCode(),is(HttpStatus.FORBIDDEN));

    }

    @Test
    public void editMessage_badId_status404() throws JwtFormatException, AuthenticationException, UnauthorizedMessageAccessException, MissingMessageException {

        String messageId = "abc123";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION,"Bearer " + BoardServiceTests.JWT_VALID);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        Message message = new Message();
        message.setBody("Hello");
        HttpEntity<Message> messageHttpEntity = new HttpEntity<>(message,headers);
        when(boardService.editMessage(any(Message.class),anyString(),anyString())).thenThrow(MissingMessageException.class);


        ResponseEntity<ErrorModel> response = this.template.exchange("http://localhost:" + port+ "/messages/"+messageId, HttpMethod.PUT,messageHttpEntity,ErrorModel.class);
        assertThat(response.getStatusCode(),is(HttpStatus.NOT_FOUND));

    }

    @Test
    public void deleteMessage_unauthorizedAccess_status403() throws JwtFormatException, AuthenticationException, UnauthorizedMessageAccessException, MissingMessageException {

        String messageId = "abc123";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION,"Bearer " + BoardServiceTests.JWT_VALID);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        Message message = new Message();
        message.setBody("Hello");
        HttpEntity<Message> messageHttpEntity = new HttpEntity<>(message,headers);
        doThrow(new UnauthorizedMessageAccessException()).when(boardService).deleteMessage(anyString(),anyString());

        ResponseEntity<Message> response = this.template.exchange("http://localhost:" + port+ "/messages/"+messageId, HttpMethod.DELETE,messageHttpEntity,Message.class);

        assertThat(response.getStatusCode(),is(HttpStatus.FORBIDDEN));

    }

    @Test
    public void deleteMessage_badId_status404() throws JwtFormatException, AuthenticationException, UnauthorizedMessageAccessException, MissingMessageException {

        String messageId = "abc123";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION,"Bearer " + BoardServiceTests.JWT_VALID);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        Message message = new Message();
        message.setBody("Hello");
        HttpEntity<Message> messageHttpEntity = new HttpEntity<>(message,headers);
        doThrow(new MissingMessageException()).when(boardService).deleteMessage(anyString(),anyString());

        ResponseEntity<Message> response = this.template.exchange("http://localhost:" + port+ "/messages/"+messageId, HttpMethod.DELETE,messageHttpEntity,Message.class);

        assertThat(response.getStatusCode(),is(HttpStatus.NOT_FOUND));

    }

    @Test
    public void deleteMessage_validRequest_status200() {

        String messageId = "abc123";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION,"Bearer " + BoardServiceTests.JWT_VALID);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        Message message = new Message();
        message.setBody("Hello");
        HttpEntity<Message> messageHttpEntity = new HttpEntity<>(message,headers);

        ResponseEntity<Message> response = this.template.exchange("http://localhost:" + port+ "/messages/"+messageId, HttpMethod.DELETE,messageHttpEntity,Message.class);
        assertThat(response.getStatusCode(),is(HttpStatus.NO_CONTENT));

    }

}
