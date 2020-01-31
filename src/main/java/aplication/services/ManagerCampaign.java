package aplication.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static aplication.configuration.HashMapCampaignConfig.KEY_MAP_CAMPAIGN;

@Service
public class ManagerCampaign {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ConcurrentHashMap<Long, Long> hashMapCampaigns;
    private final Logger logger = LoggerFactory.getLogger(ManagerCampaign.class);


    public ManagerCampaign(RedisTemplate<String, Object> redisTemplate, ConcurrentHashMap<Long, Long> hashMapCampaigns) {
        this.redisTemplate = redisTemplate;
        this.hashMapCampaigns = hashMapCampaigns;
    }

    private Boolean isNeedMoney(Long budgetCampaignRedis,Long budgetCampaignServer,Integer minNumber,double percent)
    {
        return budgetCampaignServer < (budgetCampaignRedis * percent) ||
                (budgetCampaignServer < minNumber && budgetCampaignRedis > 0);
    }

    private Long getMoney(Long budgetCampaignRedis,Integer money){
        return  (budgetCampaignRedis >= money) ? money : budgetCampaignRedis % money;
    }

    public void addMoney(int money){
        final double percent = 0.001;
        final Integer minimumNumber = 1000;

        redisTemplate.opsForHash().entries(KEY_MAP_CAMPAIGN).forEach((key,value) -> {
            logger.info("started task on CampaignId {}",key);
            Long serverBudget =  Optional.ofNullable(hashMapCampaigns.get(Long.parseLong(key.toString()))).orElse(0L);
            Long redisBudget = Long.parseLong(value.toString());


            // check if the server need some money from redis
            if(!isNeedMoney(redisBudget,serverBudget,minimumNumber,percent)){
                logger.info("ended task on CampaignId {} No money was added to redis account",key);
                return;
            }

            // how much money we need
            Long addBudget = getMoney(redisBudget,money);
            Long newServerBudget = serverBudget + addBudget;

            // move money from redis to server
            Long leftRedisBudget = redisTemplate.opsForHash().increment(KEY_MAP_CAMPAIGN,Integer.parseInt(String.valueOf(key)),-1 * addBudget);

            //check if we overdraft from redis
            if(Optional.of(leftRedisBudget).orElse(-1L) < 0){
                leftRedisBudget = redisTemplate.opsForHash().increment(KEY_MAP_CAMPAIGN,Integer.parseInt(String.valueOf(key)),addBudget);
                newServerBudget =  serverBudget;
            }

            logger.info("left in redis bank {}  CampaignId {}  money transaction {}  new Budget in memory {} " , leftRedisBudget,key,addBudget,newServerBudget);

            hashMapCampaigns.put(Long.parseLong(key.toString()), newServerBudget);
        });
    }
}
