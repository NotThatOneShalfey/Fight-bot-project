package FightBot.core;

import FightBot.configuration.Configuration;
import FightBot.entities.FightMessage;
import FightBot.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DeleterThread extends Thread {
    private boolean isRunning = false;

    @Override
    public void run() {
        isRunning = true;
        Map<Long, FightMessage> fightMessages = Utils.getInstance().fightMessages;
        long publicChannelId = Configuration.getInstance().getPublicChannelId();
        long historyChannelId = Configuration.getInstance().getHistoryChannelId();
        long guildId = Configuration.getInstance().getGuildId();
        ShardManager manager = Utils.getInstance().getManager();
        while (isRunning) {
            log.info("UDOLYATOR thread is alive");
            if (!fightMessages.isEmpty()) {
                for (Map.Entry<Long, FightMessage> entry : fightMessages.entrySet()) {
                    if (entry.getValue().getChannelId() == publicChannelId
                        && entry.getValue().getRegisterDate().isBefore(LocalDateTime.now().minus(Configuration.getInstance().getPublicDeleterDelay(), TimeUnit.MINUTES.toChronoUnit()))) {
                        try {
                            log.info("Public channel UDOLYATOR : message id = {}", entry.getKey());
                            manager.getGuildById(guildId)
                                    .getTextChannelById(publicChannelId)
                                    .retrieveMessageById(entry.getKey())
                                    .complete()
                                    .delete()
                                    .queue();
                            fightMessages.remove(entry.getKey());
                            Utils.getInstance().lockedFightersList.remove(entry.getValue().getFirstFighter().getId());
                            Utils.getInstance().lockedFightersList.remove(entry.getValue().getSecondFighter().getId());
                        } catch (ErrorResponseException e) {
                            e.printStackTrace();
                        }
                    }
                    if (entry.getValue().getChannelId() == historyChannelId
                        && entry.getValue().isInProcess()
                        && entry.getValue().getRegisterDate().isBefore(LocalDateTime.now().minus(Configuration.getInstance().getHistoryDeleterDelay(), TimeUnit.MINUTES.toChronoUnit()))) {
                        try {
                            log.info("History channel UDOLYATOR : message id = {}", entry.getKey());
                            Utils.getInstance()
                                    .getManager()
                                    .getGuildById(Configuration.getInstance().getGuildId())
                                    .getTextChannelById(Configuration.getInstance().getHistoryChannelId())
                                    .retrieveMessageById(entry.getKey())
                                    .complete()
                                    .delete()
                                    .queue();
                            fightMessages.remove(entry.getKey());
                            Utils.getInstance().lockedFightersList.remove(entry.getValue().getFirstFighter().getId());
                            Utils.getInstance().lockedFightersList.remove(entry.getValue().getSecondFighter().getId());
                        } catch (ErrorResponseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            try {
                TimeUnit.MINUTES.sleep(Configuration.getInstance().getDeleterSleepTimeout());
            } catch (InterruptedException e) {
                log.info("Exception at UDOLYATOR thread. {}. Stopping UDOLYATOR thread.", e.getMessage());
                isRunning = false;
            }
        }
    }
}
