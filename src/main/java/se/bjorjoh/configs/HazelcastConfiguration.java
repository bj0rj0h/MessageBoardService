package se.bjorjoh.configs;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Configures the injected hazelcast instance.
 * Message board is configured to hold up to 200 messages.
 * Messages won't be evicted until manually removed by user.
 */
@Configuration
public class HazelcastConfiguration {
    @Bean
    public Config hazelCastConfig(){
        Config config = new Config();
        config.setInstanceName("message-board-instance")
                .addMapConfig(
                        new MapConfig()
                                .setName("messageBoardConfig")
                                .setMaxSizeConfig(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
                                .setEvictionPolicy(EvictionPolicy.NONE)
                                .setTimeToLiveSeconds(0));
        return config;
    }
}