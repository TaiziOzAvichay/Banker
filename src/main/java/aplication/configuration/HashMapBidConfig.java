package aplication.configuration;

import aplication.entities.Bid;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class HashMapBidConfig {

    @Bean
    public ConcurrentHashMap<Long, Bid> HashMapBids (){
        return new ConcurrentHashMap<>();
    }
}
