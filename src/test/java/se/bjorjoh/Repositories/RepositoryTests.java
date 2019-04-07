package se.bjorjoh.Repositories;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
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
        sample1.setMessageId("abc123");
        sample1.setLastUpdated("someTime");
        sample1.setCreator("user@example.com");
        hazelcastRepository.saveMessage(sample1);

        Message sample2 = new Message();
        sample2.setBody("Hello");
        sample2.setMessageId("abc123");
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


        List<Message> messages = hazelcastRepository.getMessages();

        messages.forEach(message->{
          if (message.getMessageId().equals("abc123")){
              assertTrue(message.getCreator().equals("user@example.com"));
          }
        });

    }




}
