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

import static org.junit.Assert.assertFalse;


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
    }

    @Test
    public void addMessage_validMessageValidJwt_messageReturned() {

        Message message = new Message();
        message.setBody("Hello");

        String uuid = hazelcastRepository.saveMessage(message);
        assertFalse(uuid.isEmpty());

    }




}
