package FightBot.threads;

import FightBot.core.RankCalculation;
import FightBot.entities.Fighter;
import FightBot.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@EnableScheduling
public class RankDowngradeScheduler {

    private static RankCalculation rankCalculation;

    @Scheduled(cron = "0 1 0 * * Mon")
    //@Scheduled(cron = "0 * * * * *")
    private void scheduleDowngrade() {
        log.info("Schedule rank downgrade");
        for (Map.Entry<Long, Fighter> entry : Utils.getInstance().fighters.entrySet()) {
            if (entry.getValue().getRank() != 0L) {
                entry.getValue().setRank(entry.getValue().getRank() - 1L);
                rankCalculation.checkRankRoles(entry.getValue());
            }
        }
    }

    public static void initRankDowngradeScheduler() {
        rankCalculation = new RankCalculation(Utils.getInstance().getManager().getGuildById(FightBot.configuration.Configuration.getInstance().getGuildId()));
    }
}
