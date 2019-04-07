package se.bjorjoh.services;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.bjorjoh.models.JwtContent;
import se.bjorjoh.models.Message;
import se.bjorjoh.repositories.BoardRepository;
import se.bjorjoh.repositories.HazelcastRepository;

import java.io.IOException;
import java.util.Base64;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    Logger logger = LoggerFactory.getLogger(BoardService.class);

    @Autowired
    public BoardService (HazelcastRepository hazelcastRepository){
        boardRepository = hazelcastRepository;
    }

    public Message addMessage(Message message,String jwtString) throws IOException,JWTVerificationException{
        try {
            DecodedJWT jwt = JwtAuthorizer.getAndVerifyJWT(jwtString);
            JwtContent jwtContent = getJwtContent(jwt.getPayload());
            logger.info(jwtContent.getGivenName());
        } catch (JWTVerificationException e){
            logger.error("Error while validating jwt signature",e);
            throw e;
        }catch (IOException e){
            throw e;
        }
        boardRepository.saveMessage(message);
        return message;
    }

    private JwtContent getJwtContent(String jwtPayload) throws IOException{
        byte[] decodedBytes = Base64.getDecoder().decode(jwtPayload);
        String plainTextJwt = new String(decodedBytes,"UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        try {
            JwtContent content = mapper.readValue(plainTextJwt,JwtContent.class);
            return content;
        } catch (IOException e) {
            logger.error("Error while decoding jwt payload",e);
            throw e;
        }
    }
}
