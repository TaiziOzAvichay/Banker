package aplication.services;

import aplication.entities.Bid;
import aplication.reposetories.BidRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import javax.validation.constraints.NotNull;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ManagerBid {

    final BidRepository bidRepository;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ConcurrentHashMap<Long,Long> hashMapCampaign;

    public ManagerBid(BidRepository bidRepository,
                      RedisTemplate<String, Object> redisTemplate,
                      ConcurrentHashMap<Long, Long> hashMapCampaign) {

        this.bidRepository = bidRepository;
        this.redisTemplate = redisTemplate;
        this.hashMapCampaign = hashMapCampaign;
    }


    public Boolean newBid(Bid bid)
    {
        if (bid.getCampaignId() == null ||
            !hashMapCampaign.containsKey(bid.getCampaignId()) ||
            hashMapCampaign.get(bid.getBidId()) < bid.getCost()) {

            return false;
        }

        long budget = hashMapCampaign.get(bid.getBidId());
        hashMapCampaign.put(bid.getBidId(),budget - bid.getCost());
        bid.setStatus(Bid.Status.UNKNOWN);
        bidRepository.save(bid);

        return true;
    }

    public Boolean updateBid(@NotNull String bidId, @NotNull Bid.Status status)
    {
        Bid bid = bidRepository.findById(bidId).orElse(null);
        if (bid == null ||
            Bid.Status.UNKNOWN == status ||
            bid.getStatus() != Bid.Status.UNKNOWN)
            return false;

        bid.setStatus(status);
        if(status == Bid.Status.LOSE){
            long budget = hashMapCampaign.get(bid.getCampaignId()) + bid.getCost();
            hashMapCampaign.put(bid.getCampaignId(),budget);
        }

        bidRepository.save(bid);


        return  true;
    }
}
