package se.bjorjoh.controllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import se.bjorjoh.ErrorHandling.AuthorizationException;
import se.bjorjoh.models.ErrorModel;
import se.bjorjoh.models.Message;
import se.bjorjoh.services.BoardService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@RestController
public class Controller {

    @Autowired
    private BoardService boardService;

    @ExceptionHandler({JWTVerificationException.class})
    public ErrorModel handleInvalidJwt(HttpServletResponse response){

        TimeZone tz = TimeZone.getDefault();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());

        ErrorModel error = new ErrorModel("Error while verifying jwt signature",nowAsISO);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return error;
    }

    @ExceptionHandler({AuthorizationException.class})
    public ErrorModel handleMalformedAuthorizationHeader(HttpServletResponse response){

        TimeZone tz = TimeZone.getDefault();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());

        ErrorModel error = new ErrorModel("Please make sure authorization header is provided with a valid token",nowAsISO);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return error;
    }


    @RequestMapping(value = "/messages",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Message> getMessages(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        return new ArrayList<Message>();
    }

    @RequestMapping(value = "/messages",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Message addMessage(@Valid @RequestBody Message message,
                              @RequestHeader(value = "Authorization",required = false) String authorizationHeader,
                              HttpServletResponse response) throws IOException,JWTVerificationException,
                                                            AuthorizationException{

        if (authorizationHeader == null){
            throw new AuthorizationException("Authorization header missing");
        }
        String jwt = extractJwtFromAuthorizationHeader(authorizationHeader);

        if (jwt.isEmpty()){
            throw new AuthorizationException("Authorization header is missing bearer prefix or is badly formatted");
        }
        Message createdMessage = boardService.addMessage(message,jwt);
        response.setStatus(HttpServletResponse.SC_CREATED);
        return createdMessage;

    }

    @RequestMapping(value = "/messages/{messageId}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Message editMessage(@PathVariable("messageId") String messageId,HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        return new Message();
    }

    @RequestMapping(value = "/messages/{messageId}",method = RequestMethod.DELETE)
    public void deleteMessage(@PathVariable("messageId") String messageId,HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);

    }

    private String extractJwtFromAuthorizationHeader(String authorizationHeader) {
        String[] split = authorizationHeader.split("\\s+");
        String result = "";
        if (split.length>1){
            result = split[1];
        }
        return result;

    }

}