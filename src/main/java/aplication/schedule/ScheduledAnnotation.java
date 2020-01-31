package aplication.schedule;

import aplication.services.ManagerCampaign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledAnnotation {
    private final Logger logger = LoggerFactory.getLogger(ScheduledAnnotation.class);
    private final ManagerCampaign managerCampaign;

    public ScheduledAnnotation(ManagerCampaign managerCampaign) {
        this.managerCampaign = managerCampaign;
    }

    @Scheduled(fixedDelay = 2000, initialDelay = 4000)
    public void scheduleFixedRateWithInitialDelayTask() {
        logger.info("started scheduleFixedRateWithInitialDelayTask");
        final int money = 500;
        managerCampaign.addMoney(money);

        logger.info("ended scheduleFixedRateWithInitialDelayTask");
    }
}


