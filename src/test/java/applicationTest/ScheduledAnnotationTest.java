package applicationTest;

import aplication.BankerServer;

import aplication.schedule.ScheduledAnnotation;
import aplication.services.ManagerCampaign;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.awaitility.Durations;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest(classes = BankerServer.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class ScheduledAnnotationTest {

    @SpyBean
    private ScheduledAnnotation scheduledAnnotation;

    @MockBean
    private ManagerCampaign managerCampaign;

    @Test
    public void zeroScheduled() {
        await().atMost(Durations.ONE_SECOND)
                .untilAsserted(() -> verify(scheduledAnnotation, times(0)).scheduleFixedRateWithInitialDelayTask());
    }

    @Test
    public void justOneScheduled() {
        await().atMost(Durations.FIVE_SECONDS)
                .untilAsserted(() -> verify(scheduledAnnotation, times(1)).scheduleFixedRateWithInitialDelayTask());
    }

    @Test
    public void tenScheduled() {
        await().atMost(Durations.TEN_SECONDS)
                .untilAsserted(() -> verify(scheduledAnnotation, times(3)).scheduleFixedRateWithInitialDelayTask());
    }
}
