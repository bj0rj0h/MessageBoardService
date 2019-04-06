package se.bjorjoh.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import se.bjorjoh.models.Message;
import se.bjorjoh.services.BoardService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class Controller {

    @Autowired
    private BoardService boardService;

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
                              HttpServletResponse response) {

        boardService.addMessage(message);
        response.setStatus(HttpServletResponse.SC_CREATED);
        return new Message();
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

}