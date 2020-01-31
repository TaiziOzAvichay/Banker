package aplication.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class HashMapCampaignConfig {

    private final Logger logger = LoggerFactory.getLogger(HashMapCampaignConfig.class);
    public static final String KEY_MAP_CAMPAIGN = "Campaign";
    private final RedisTemplate<String, Object> redisTemplate;

    public HashMapCampaignConfig(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Bean
    public ConcurrentHashMap<Long, Long> hashMapCampaigns (HashMap<Long, Long> Campaign){
        ConcurrentHashMap<Long, Long> concurrentHashMapCampaign = new ConcurrentHashMap<>();
        insertCampToRedisTemplate(redisTemplate,Campaign);

        return  concurrentHashMapCampaign;
    }


    private void insertCampToRedisTemplate(RedisTemplate<String, Object> redisTemplate, HashMap<Long, Long> campaigns) {
        campaigns.forEach((key,value) -> redisTemplate.boundHashOps(KEY_MAP_CAMPAIGN).put(key,value));
    }


}
