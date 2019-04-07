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
import org.springframework.test.context.junit4.SpringRunner;
import se.bjorjoh.models.Message;
import se.bjorjoh.repositories.BoardRepository;
import se.bjorjoh.repositories.HazelcastRepository;


@RunWith(SpringRunner.class)
public class RepositoryTests {


    private static BoardRepository boardRepository;

    @BeforeClass
    public static void setUp(){
        Config config = new Config();
        config.setInstanceName("message-board-instance")
                .addMapConfig(
                        new MapConfig()
                                .setName("messageBoardConfig")
                                .setMaxSizeConfig(new MaxSizeConfig(10, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
                                .setEvictionPolicy(EvictionPolicy.NONE)
                                .setTimeToLiveSeconds(0));
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        boardRepository = new HazelcastRepository(hazelcastInstance);
    }

    @Test
    public void addMessage_validMessageValidJwt_messageReturned() {

        Message message = new Message();
        message.setBody("Hello");

        boardRepository.saveMessage(message);


    }

}
