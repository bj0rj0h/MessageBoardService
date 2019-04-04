package se.bjorjoh.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import se.bjorjoh.models.Message;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
public class Controller {

    @RequestMapping(value = "/messages",method = RequestMethod.GET)
    public List<Message> getMessages(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        return new ArrayList<Message>();
    }

    @RequestMapping(value = "/messages",method = RequestMethod.POST)
    public Message addMessage(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_CREATED);
        return new Message();
    }

    @RequestMapping(value = "/messages/{messageId}",method = RequestMethod.PUT)
    public Message editMessage(@PathVariable("messageId") String messageId,HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        return new Message();
    }

    @RequestMapping(value = "/messages/{messageId}",method = RequestMethod.DELETE)
    public void deleteMessage(@PathVariable("messageId") String messageId,HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);

    }

}