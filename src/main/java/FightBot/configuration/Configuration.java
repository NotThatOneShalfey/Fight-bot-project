package FightBot.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@org.springframework.context.annotation.Configuration
@ConfigurationProperties(prefix = "fight-bot")
@Slf4j
public class Configuration {
    @Getter
    private static Configuration instance;

    private boolean inDebugMode = false;
    private final String prefix = "-";
    private Long ownerId = 329388314333282304L;
    private long guildId;
    private long publicChannelId;
    private long historyChannelId;
    private long publicDeleterDelay;
    private long historyDeleterDelay;
    private long DeleterSleepTimeout = 10;
    private long SaverSleepTimeout = 30;
    private final String token = "OTA0MDk4NzIxNzE1ODYzNTcy.YX2ljA.ZA46m4URI_OZ2bLqWSMcXdoA5HQ";
    private List<Long> referees = new ArrayList<>();
    public Map<Long, Long> rankingsMap = new HashMap<>();
    public List<Long> titlesList = new ArrayList<>();
    private Long rankDifference = 2L;
    public static final String FIGHT_DATES_MESSAGES_PATH = System.getProperty("user.dir") + File.separator + "fight_dates.txt";
    public static final String FIGHT_MESSAGES_PATH = System.getProperty("user.dir") + File.separator + "fight_messages.txt";
    public static final String LOCKED_FIGHTERS_PATH = System.getProperty("user.dir") + File.separator + "locked_fighters.txt";
    public static final String FIGHTERS_PATH = System.getProperty("user.dir") + File.separator + "fighters.txt";

    @PostConstruct
    public void init() {

        rankingsMap.put(1L, 904906006381887499L);
        rankingsMap.put(2L, 904906152591102053L);
        rankingsMap.put(3L, 904906200120950814L);
        rankingsMap.put(4L, 904906248128987156L);
        rankingsMap.put(5L, 904906280576090123L);
        rankingsMap.put(6L, 904906326856040448L);

        titlesList.add(905946883954970634L);
        titlesList.add(905946961331507221L);

        instance = this;
    }
}
