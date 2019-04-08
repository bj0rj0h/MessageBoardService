package se.bjorjoh.Repositories;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.And;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import se.bjorjoh.models.Message;
import se.bjorjoh.repositories.HazelcastRepository;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
public class RepositoryTests {


    @Autowired
    private static HazelcastRepository hazelcastRepository;

    @BeforeClass
    public static void setUp(){
        Config config = new Config();
        config.setInstanceName("message-board-test-instance")
                .addMapConfig(
                        new MapConfig()
                                .setName("messageBoardTestConfig")
                                .setMaxSizeConfig(new MaxSizeConfig(10, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
                                .setEvictionPolicy(EvictionPolicy.NONE)
                                .setTimeToLiveSeconds(0));
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        hazelcastRepository = new HazelcastRepository(hazelcastInstance);


        Message sample1 = new Message();
        sample1.setBody("Hello");
        sample1.setLastUpdated("someTime");
        sample1.setCreator("user@example.com");
        hazelcastRepository.saveMessage(sample1);

        Message sample2 = new Message();
        sample2.setBody("Hello2");
        sample2.setLastUpdated("someTime");
        sample2.setCreator("user@example.com");
        hazelcastRepository.saveMessage(sample2);

    }

    @Test
    public void addMessage_validMessageValidJwt_messageReturned() {

        Message message = new Message();
        message.setBody("Hello");

        String uuid = hazelcastRepository.saveMessage(message);
        assertFalse(uuid.isEmpty());

    }

    @Test
    public void getMessages_validRequest_nonEmptyMessageListReturned() {


        List<Message> messages = hazelcastRepository.getMessages();
        assertFalse(messages.isEmpty());

    }

    @Test
    public void getMessages_validRequest_expectedMessageToBeInList() {


        Message sample = new Message();
        sample.setBody("Hello");
        sample.setLastUpdated("someTime");
        sample.setCreator("test@example.com");
        String id= hazelcastRepository.saveMessage(sample);

        List<Message> messages = hazelcastRepository.getMessages();


        messages.forEach(message -> {
            if (message.getMessageId().equals(id)){
                Assert.assertEquals(message.getCreator(),"test@example.com");
            }
        });


    }

    @Test
    public void editMessages_validRequest_expectedMessageToBeInList() {

        Message sample3 = new Message();
        sample3.setBody("Hello2");
        sample3.setMessageId("abc222");
        sample3.setLastUpdated("someTime");
        sample3.setCreator("user@example.com");
        String id = hazelcastRepository.saveMessage(sample3);

        Message newMessage = new Message();
        newMessage.setBody("New Hello");
        newMessage.setMessageId("abc333");
        newMessage.setLastUpdated("someTime");
        newMessage.setCreator("user@example.com");
        hazelcastRepository.editMessage(id,newMessage);
        Message editedMessage = hazelcastRepository.getMessage(id);


        assertTrue(editedMessage.getBody().equals(newMessage.getBody()));



    }




}
