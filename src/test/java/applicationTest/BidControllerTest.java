package applicationTest;

import aplication.BankerServer;
import aplication.entities.Bid;
import aplication.reposetories.BidRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.ConcurrentHashMap;
import static aplication.configuration.HashMapCampaignConfig.KEY_MAP_CAMPAIGN;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.CoreMatchers.is;
import static util.JsonUtil.asJsonString;

@TestPropertySource(properties = "app.scheduling.enable=false")
@SpringBootTest(classes = BankerServer.class,webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@RunWith(SpringJUnit4ClassRunner.class)
public class BidControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private  RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ConcurrentHashMap<Long, Long> hashMapCampaigns;

    @Before
    public void beforeEveryTest()
    {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        hashMapCampaigns.clear();
    }

    @After
    public void AfterEveryTest()
    {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        hashMapCampaigns.clear();
    }

    @Test
    public void givenBid_whenNoCampaignId_thenFalseStatus200() throws Exception {
        //arrange
        Bid bid = Bid.builder().bidId(1L).campaignId(1L).cost(50).status(Bid.Status.UNKNOWN).build();

        //act
        mvc.perform(post("/bid")
               .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bid)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(false)));
    }

    @Test
    public void givenBid_whenNoMoney_thenFalseStatus200() throws Exception {
        //arrange
        Bid bid = Bid.builder().bidId(1L).campaignId(1L).cost(50).status(Bid.Status.UNKNOWN).build();
        hashMapCampaigns.put(1L,0L);


        //act
        mvc.perform(post("/bid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bid)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(false)));
    }

    @Test
    public void givenBid_whenNoMoneyOnServerButHaveInRedis_thenFalseStatus200() throws Exception {
        //arrange
        Bid bid = Bid.builder().bidId(1L).campaignId(1L).cost(50).status(Bid.Status.UNKNOWN).build();
        hashMapCampaigns.put(1L,0L);
        redisTemplate.boundHashOps(KEY_MAP_CAMPAIGN).put(1,200000L);

        //act
        mvc.perform(post("/bid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bid)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(false)));
    }


    @Test
    public void givenBid_whenHasMoneyOnServerButNotInRedis_thenFalseStatus200() throws Exception {
        //arrange
        Bid bid = Bid.builder().bidId(1L).campaignId(1L).cost(50).status(Bid.Status.UNKNOWN).build();
        hashMapCampaigns.put(1L,100L);
        redisTemplate.boundHashOps(KEY_MAP_CAMPAIGN).put(1,0L);
        Long serverMoney = hashMapCampaigns.get(1L);


        //act
        mvc.perform(post("/bid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bid)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(true)));

        assertThat(serverMoney ,equalTo(hashMapCampaigns.get(1L) + bid.getCost()));
    }

    @Test
    public void givenBid_whenNotEnoughMoneyOnServerButNotInRedis_thenFalseStatus200() throws Exception {
        //arrange
        Bid bid = Bid.builder().bidId(1L).campaignId(1L).cost(50).status(Bid.Status.UNKNOWN).build();
        hashMapCampaigns.put(1L,20L);
        Long serverMoney = hashMapCampaigns.get(1L);


        //act
        mvc.perform(post("/bid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bid)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(false)));

        assertThat(serverMoney ,equalTo(hashMapCampaigns.get(1L)));
    }


    @Test
    public void updateBidLose_getMoney_thenTrueStatus200() throws Exception {
        //arrange
        Bid bid = Bid.builder().bidId(1L).campaignId(1L).cost(50).status(Bid.Status.UNKNOWN).build();
        bidRepository.save(bid);
        hashMapCampaigns.put(1L,20L);
        Long serverMoney = hashMapCampaigns.get(1L);

        //act
        mvc.perform(post("/bid/{bidId}/{status}",bid.getBidId(), Bid.Status.LOSE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(true)));

        long actual = hashMapCampaigns.get(1L);
        long expected = serverMoney + bid.getCost();

        assertThat(actual ,equalTo(expected));
    }

    @Test
    public void updateBidWin_NoMoney_thenTrueStatus200() throws Exception {
        //arrange
        Bid bid = Bid.builder().bidId(1L).campaignId(1L).cost(50).status(Bid.Status.UNKNOWN).build();
        bidRepository.save(bid);
        hashMapCampaigns.put(1L,20L);
        Long serverMoney = hashMapCampaigns.get(1L);

        //act
        mvc.perform(post("/bid/{bidId}/{status}",bid.getBidId(), Bid.Status.WIN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(true)));

        long actual = hashMapCampaigns.get(1L);
        long expected = serverMoney;

        assertThat(actual ,equalTo(expected));
    }

    @Test
    public void updateBid_NoUpdate_thenTrueStatus200() throws Exception {
        //arrange
        Bid bid = Bid.builder().bidId(1L).campaignId(1L).cost(50).status(Bid.Status.UNKNOWN).build();
        bidRepository.save(bid);
        hashMapCampaigns.put(1L,20L);
        Long serverMoney = hashMapCampaigns.get(1L);

        //act
        mvc.perform(post("/bid/{bidId}/{status}",bid.getBidId(), Bid.Status.WIN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(true)));

        long actual = hashMapCampaigns.get(1L);
        long expected = serverMoney;

        assertThat(actual ,equalTo(expected));
    }
}
