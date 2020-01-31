package aplication.Controller;

import aplication.entities.Bid;
import aplication.services.ManagerBid;
import org.springframework.web.bind.annotation.*;

@RestController
public class BidController {

    private final ManagerBid managerBid;

    public BidController(ManagerBid managerBid) {
        this.managerBid = managerBid;
    }

    @PostMapping("/bid/{id}/{status}")
    public Boolean updateBid(@PathVariable("id") String id, @PathVariable("status") Bid.Status status) {
        return managerBid.updateBid(id,status);
    }

    @PostMapping("/bid")
    public Boolean newBid(@RequestBody Bid newBid) {
        return  managerBid.newBid(newBid);
    }
}
