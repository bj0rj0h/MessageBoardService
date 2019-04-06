package se.bjorjoh.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.bjorjoh.models.Message;
import se.bjorjoh.repositories.BoardRepository;
import se.bjorjoh.repositories.HazelcastRepository;

@Service
public class BoardService {

    private final BoardRepository boardRepository;


    @Autowired
    public BoardService (HazelcastRepository hazelcastRepository){
        boardRepository = hazelcastRepository;
    }

    public Message addMessage(Message message){
        boardRepository.saveMessage(message);
        return message;
    }
}
