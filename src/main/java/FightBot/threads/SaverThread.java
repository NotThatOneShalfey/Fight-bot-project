package FightBot.threads;

import FightBot.configuration.Configuration;
import FightBot.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SaverThread extends Thread{
    private boolean isRunning = false;

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                log.info("SOHRANYATOR thread is alive.");
                Utils.getInstance().saveHistory();
                TimeUnit.MINUTES.sleep(Configuration.getInstance().getSaverSleepTimeout());
            } catch (InterruptedException | IOException e) {
                log.info("Exception at SOHRANYATOR thread. {}. Stopping SOHRANYATOR thread.", e.getMessage());
                isRunning = false;
            }
        }
    }
}
