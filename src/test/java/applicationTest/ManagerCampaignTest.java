package applicationTest;

import aplication.BankerServer;
import aplication.services.ManagerCampaign;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.util.Assert;
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.BDDMockito.*;
//import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static aplication.configuration.HashMapCampaignConfig.KEY_MAP_CAMPAIGN;


@TestPropertySource(properties = "app.scheduling.enable=false")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = BankerServer.class)
@RunWith(SpringJUnit4ClassRunner.class)

public class ManagerCampaignTest {

    private final Logger logger = LoggerFactory.getLogger(ManagerCampaignTest.class);


    @Autowired
    private ManagerCampaign managerCampaign;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private  ConcurrentHashMap<Long, Long> hashMapCampaigns;

    @Before
    public void beforeEveryTest()
    {
        hashMapCampaigns.clear();
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        redisTemplate.boundHashOps(KEY_MAP_CAMPAIGN).put(1,200000L);

    }

    @After
    public void AfterEveryTest()
    {
        hashMapCampaigns.clear();
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    public void tryToAddMoneyWhenYouDontNeed()
    {
        //arrange
        Integer redisMoney =
                Integer.parseInt(((Map)redisTemplate.opsForHash().entries(KEY_MAP_CAMPAIGN)).values().stream().findFirst().orElse(0).toString());

        hashMapCampaigns.put(1L,100000L);

        //act
        managerCampaign.addMoney(100);

        Integer serverMoney = Integer.parseInt(hashMapCampaigns.get(1L).toString());

        Integer redisMoneyAfterTransaction =
                Integer.parseInt(((Map)redisTemplate.opsForHash().entries(KEY_MAP_CAMPAIGN)).values().stream().findFirst().orElse(0).toString());

        assertThat(redisMoney ,equalTo(redisMoneyAfterTransaction));
    }


    @Test
    public void AskMore()
    {
        //arrange
        Integer redisMoney =
                Integer.parseInt(((Map)redisTemplate.opsForHash().entries(KEY_MAP_CAMPAIGN)).values().stream().findFirst().orElse(0).toString());

        //act
        managerCampaign.addMoney(10000000);

        Integer serverMoney = Integer.parseInt(hashMapCampaigns.get(1L).toString());

        Integer redisMoneyAfterTransaction =
                Integer.parseInt(((Map)redisTemplate.opsForHash().entries(KEY_MAP_CAMPAIGN)).values().stream().findFirst().orElse(0).toString());

        assertThat(redisMoney ,equalTo(serverMoney));
        assertThat(redisMoneyAfterTransaction ,equalTo(0));
    }

    @Test
    public void addLessThanMinMoney()
    {
        //arrange
        Integer redisMoney =
                Integer.parseInt(((Map)redisTemplate.opsForHash().entries(KEY_MAP_CAMPAIGN)).values().stream().findFirst().orElse(0).toString());

        managerCampaign.addMoney(100);

        Integer serverMoney = Integer.parseInt(hashMapCampaigns.get(1L).toString());

        Integer redisMoneyAfterTransaction =
                Integer.parseInt(((Map)redisTemplate.opsForHash().entries(KEY_MAP_CAMPAIGN)).values().stream().findFirst().orElse(0).toString());


        assertThat(redisMoney ,equalTo(redisMoneyAfterTransaction + serverMoney));
    }
    @Test
    public void addMinMoney()
    {
        Integer redisMoney =
                Integer.parseInt(((Map)redisTemplate.opsForHash().entries(KEY_MAP_CAMPAIGN)).values().stream().findFirst().orElse(0).toString());

        managerCampaign.addMoney(1000);

        Integer serverMoney = Integer.parseInt(hashMapCampaigns.get(1L).toString());

        Integer redisMoneyAfterTransaction =
                Integer.parseInt(((Map)redisTemplate.opsForHash().entries(KEY_MAP_CAMPAIGN)).values().stream().findFirst().orElse(0).toString());

        assertThat(redisMoney ,equalTo(redisMoneyAfterTransaction + serverMoney));
    }

    @Test
    public void addMoreThanMinMoney()
    {
        Integer redisMoney =
                Integer.parseInt(((Map)redisTemplate.opsForHash().entries(KEY_MAP_CAMPAIGN)).values().stream().findFirst().orElse(0).toString());

        managerCampaign.addMoney(10000);

        Integer serverMoney = Integer.parseInt(hashMapCampaigns.get(1L).toString());

        Integer redisMoneyAfterTransaction =
                Integer.parseInt(((Map)redisTemplate.opsForHash().entries(KEY_MAP_CAMPAIGN)).values().stream().findFirst().orElse(0).toString());

        assertThat(redisMoney ,equalTo(redisMoneyAfterTransaction + serverMoney));
    }




}
