package se.bjorjoh.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import se.bjorjoh.ErrorHandling.AuthenticationException;
import se.bjorjoh.ErrorHandling.JwtFormatException;
import se.bjorjoh.ErrorHandling.MissingMessageException;
import se.bjorjoh.ErrorHandling.UnauthorizedMessageAccessException;
import se.bjorjoh.models.ErrorModel;
import se.bjorjoh.models.Message;
import se.bjorjoh.services.BoardService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@RestController
public class Controller {

    @Autowired
    private BoardService boardService;

    @ExceptionHandler({MissingMessageException.class})
    public ErrorModel handleMissingMessage(HttpServletResponse response){

        String nowAsISO = getCurrentTimeAsISO8601();
        ErrorModel error = new ErrorModel("No message exists with the given id",nowAsISO);
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return error;
    }

    @ExceptionHandler({JwtFormatException.class})
    public ErrorModel handleMalformedJwtPayload(HttpServletResponse response){

        String nowAsISO = getCurrentTimeAsISO8601();
        ErrorModel error = new ErrorModel("Error while parsing jwt body",nowAsISO);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return error;
    }

    @ExceptionHandler({AuthenticationException.class})
    public ErrorModel handleMalformedAuthorizationHeader(HttpServletResponse response){

        String nowAsISO = getCurrentTimeAsISO8601();

        ErrorModel error = new ErrorModel("Please make sure authorization header is provided with a valid token",nowAsISO);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return error;
    }

    @ExceptionHandler({UnauthorizedMessageAccessException.class})
    public ErrorModel handleUnauthorizedAccess(HttpServletResponse response){

        String nowAsISO = getCurrentTimeAsISO8601();

        ErrorModel error = new ErrorModel("User does not have the rights to access the requested resource",nowAsISO);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return error;
    }


    @RequestMapping(value = "/messages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Message> getMessages(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        List<Message> result = boardService.getAllMessages();
        return result;
    }

    @RequestMapping(value = "/messages", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Message addMessage(@Valid @RequestBody Message message,
                              @RequestHeader(value = "Authorization",required = false) String authorizationHeader,
                              HttpServletResponse response) throws IOException,
                                  AuthenticationException {

        String jwt = verifyAuthorizationHeaderIsValid(authorizationHeader);

        Message createdMessage = boardService.addMessage(message,jwt);
        response.setStatus(HttpServletResponse.SC_CREATED);
        return createdMessage;

    }

    @RequestMapping(value = "/messages/{messageId}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Message editMessage(@PathVariable("messageId") String messageId,
                               @Valid @RequestBody Message message,
                               @RequestHeader(value = "Authorization",required = false) String authorizationHeader,
                               HttpServletResponse response) throws JwtFormatException,
            UnauthorizedMessageAccessException, AuthenticationException, MissingMessageException {

        String jwt = verifyAuthorizationHeaderIsValid(authorizationHeader);
        Message editedMessage = boardService.editMessage(message,messageId,jwt);
        response.setStatus(HttpServletResponse.SC_OK);
        return editedMessage;
    }

    @RequestMapping(value = "/messages/{messageId}",method = RequestMethod.DELETE)
    public void deleteMessage(@PathVariable("messageId") String messageId,
                              @RequestHeader(value = "Authorization",required = false) String authorizationHeader,
                              HttpServletResponse response)
                                throws  AuthenticationException,
                                        JwtFormatException,
                                        MissingMessageException,
                                        UnauthorizedMessageAccessException {

        String jwt = verifyAuthorizationHeaderIsValid(authorizationHeader);
        boardService.deleteMessage(messageId,jwt);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private String extractJwtFromAuthorizationHeader(String authorizationHeader) {

        String[] split = authorizationHeader.split("\\s+");
        String result = "";
        if (split.length>1){
            result = split[1];
        }
        return result;

    }

    public String verifyAuthorizationHeaderIsValid(String authorizationHeader) throws AuthenticationException {

        if (authorizationHeader == null){
            throw new AuthenticationException("Authorization header missing");
        }

        String jwt = extractJwtFromAuthorizationHeader(authorizationHeader);
        if (jwt.isEmpty()){
            throw new AuthenticationException("Authorization header is missing bearer prefix or is badly formatted");
        }
        return jwt;

    }

    private String getCurrentTimeAsISO8601(){

        TimeZone tz = TimeZone.getDefault();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        return nowAsISO;

    }

}