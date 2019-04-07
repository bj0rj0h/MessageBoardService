package se.bjorjoh.Controllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.DefaultResponseErrorHandler;
import se.bjorjoh.Services.BoardServiceTests;
import se.bjorjoh.models.Message;
import se.bjorjoh.services.BoardService;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ComponentScan("se.bjorjoh.controllers")
@ComponentScan("se.bjorjoh.services")
@TestPropertySource(locations = "/test.properties")
public class ControllerTests {


    @MockBean
    private BoardService boardService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;
    

    @Test
    public void addMessage_validMessage_status201(){

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION,"Bearer " + BoardServiceTests.JWT_VALID);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        Message message = new Message();
        message.setBody("Hello");
        HttpEntity<Message> messageHttpEntity = new HttpEntity<>(message,headers);
        ResponseEntity<Message> response = this.template.exchange("http://localhost:" + port+ "/messages", HttpMethod.POST,messageHttpEntity,Message.class);
        assertThat(response.getStatusCode(),is(HttpStatus.CREATED));

    }

    @Test
    public void addMessage_invalidJwtSignature_status201() throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION,"Bearer " + BoardServiceTests.JWT_INVALID_SIGNATURE);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);


        when(boardService.addMessage(any(Message.class),anyString())).thenThrow(JWTVerificationException.class);

        Message message = new Message();
        message.setBody("Hello");
        HttpEntity<Message> messageHttpEntity = new HttpEntity<>(message,headers);
        ResponseEntity<Message> response = this.template.exchange("http://localhost:" + port+ "/messages", HttpMethod.POST,messageHttpEntity,Message.class);
        assertThat(response.getStatusCode(),is(HttpStatus.UNAUTHORIZED));

    }

}
