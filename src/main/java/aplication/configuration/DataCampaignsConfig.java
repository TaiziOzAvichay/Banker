package aplication.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class DataCampaignsConfig {

    @Bean
    public HashMap<Long, Long> getCampaigns()
    {
        HashMap<Long,Long> hashMap = new HashMap<>();
        hashMap.put(1L,2301000L);
        hashMap.put(2L,250000L);
        hashMap.put(3L,2230000L);
        hashMap.put(4L,2053000L);
        hashMap.put(5L,20023400L);
        hashMap.put(6L,200123L);
        hashMap.put(7L,800000L);

        return hashMap;
    }
}
