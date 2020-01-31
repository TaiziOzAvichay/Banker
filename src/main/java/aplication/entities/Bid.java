package aplication.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Builder(toBuilder = true)
@AllArgsConstructor
@Getter
@Setter
@RedisHash("Bid")
public class Bid implements Serializable {

    @Id
    private Long bidId;

    private Long campaignId;

    private Integer cost;

    private Status status;

    public enum Status {
        WIN,
        LOSE,
        UNKNOWN
    }

    @Override
    public String toString() {

        return "Bid{" + "bidId='" + bidId + '\'' + ", campaignId='" + campaignId + '\'' + ", cost=" + cost + ", status=" + status + '}';
    }
}
