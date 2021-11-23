package FightBot.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;

@Data
@org.springframework.context.annotation.Configuration
@ConfigurationProperties(prefix = "fight-bot")
@Slf4j
public class Configuration {
    @Getter
    private static Configuration instance;

    private boolean inDebugMode = false;
    private final String prefix = "=";
    private Long ownerId = 329388314333282304L;
    private long guildId;
    private long publicChannelId;
    private long historyChannelId;
    private long rulesChannelId;
    private long ranksChannelId;
    private long publicDeleterDelay;
    private long championRankId;
    private long historyDeleterDelay;
    private long deleterSleepTimeout = 10;
    private long saverSleepTimeout = 30;
    private long activeStatusRoleId;
    private boolean onlyActiveSearch = false;
    private String token;
    private List<Long> referees = new ArrayList<>();
    public Map<Long, Long> rankingsMap = new HashMap<>();
    public List<Long> titlesList = new ArrayList<>();
    public Map<Long, Long> thresholdsMap = new HashMap<>();
    private Long rankDifference = 2L;
    public static final String FIGHT_DATES_MESSAGES_PATH = System.getProperty("user.dir") + File.separator + "fight_dates.txt";
    public static final String FIGHT_MESSAGES_PATH = System.getProperty("user.dir") + File.separator + "fight_messages.txt";
    public static final String LOCKED_FIGHTERS_PATH = System.getProperty("user.dir") + File.separator + "locked_fighters.txt";
    public static final String FIGHTERS_PATH = System.getProperty("user.dir") + File.separator + "fighters.txt";

    @PostConstruct
    public void init() {
        instance = this;
    }
}
