package FightBot.utils;

import FightBot.core.RankCalculation;
import FightBot.entities.FightDateLock;
import FightBot.entities.FightMessage;
import FightBot.entities.Fighter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration("Utils")
public class Utils {
    private final JsonMapper mapper = new JsonMapper();

    @Getter
    private static Utils instance;

    @Getter
    @Setter
    private ShardManager manager;

    private final File fightMapFile = new File(FightBot.configuration.Configuration.FIGHT_MESSAGES_PATH);
    private final File lockedFightersFile = new File(FightBot.configuration.Configuration.LOCKED_FIGHTERS_PATH);
    private final File fightersFile = new File(FightBot.configuration.Configuration.FIGHTERS_PATH);
    private final File fightDatesFile = new File(FightBot.configuration.Configuration.FIGHT_DATES_MESSAGES_PATH);
    private final File refereeListFile = new File(FightBot.configuration.Configuration.REFEREES_PATH);

    public Map<Long, Fighter> fighters = new HashMap<>();
    public Map<Long, FightMessage> fightMessages = new HashMap<>();

    public List<FightDateLock> fightDatesList = new ArrayList<>();

    @Setter
    public List<Long> lockedFightersList = new ArrayList<>();

    public static final Random RANDOM = new Random();

    public static Color getRandomColor() {
        float r = RANDOM.nextFloat();
        float g = RANDOM.nextFloat();
        float b = RANDOM.nextFloat();

        return new Color(r, g, b);
    }

    public List<Fighter> getListOfAvailableFighters(Fighter initialFighter) {
        Collection<Fighter> fightersColl = fighters.values();
        return fightersColl.stream()
                .filter(fighter ->
                        fighter.getId() != initialFighter.getId()
                        && (fighter.isActive() || !FightBot.configuration.Configuration.getInstance().isOnlyActiveSearch())
                        && fighter.getRank() - initialFighter.getRank() <= FightBot.configuration.Configuration.getInstance().getRankDifference()
                        && fighter.getThreshold() >= initialFighter.getThreshold()
                        && !fightDatesList.contains(new FightDateLock(LocalDate.now(), fighter.getId(), initialFighter.getId()))
                        && !lockedFightersList.contains(fighter.getId()))
                .collect(Collectors.toList());
    }


    public void saveHistory() throws IOException {
        generateFile(mapper.writeValueAsString(fightMessages), fightMapFile);
        generateFile(mapper.writeValueAsString(lockedFightersList), lockedFightersFile);
        generateFile(mapper.writeValueAsString(fighters), fightersFile);
        generateFile(mapper.writeValueAsString(fightDatesList), fightDatesFile);
        generateFile(mapper.writeValueAsString(FightBot.configuration.Configuration.getInstance().getReferees()), refereeListFile);
    }

    private void generateFile(String str, File file) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        writer.write(str);
        writer.flush();
        writer.close();
    }

    public void checkRolesOnCall() {
        log.info("Start roles check up");
        Guild guild = manager.getGuildById(FightBot.configuration.Configuration.getInstance().getGuildId());
        log.info("Guild : {}", guild.toString());
        for (Map.Entry<Long, Long> entry : FightBot.configuration.Configuration.getInstance().rankingsMap.entrySet()) {
            if (guild.getRoleById(entry.getValue()) == null) {
                log.warn("Role with ID = {}, rank level = {} does not exist", entry.getValue(), entry.getKey());
            }
        }
        for (Long id : FightBot.configuration.Configuration.getInstance().titlesList) {
            if (guild.getRoleById(id) == null) {
                log.warn("Title with ID = {} does not exist", id);
            }
        }
        for (Map.Entry<Long, Long> entry : FightBot.configuration.Configuration.getInstance().thresholdsMap.entrySet()) {
            if (guild.getRoleById(entry.getValue()) == null) {
                log.warn("Threshold role with ID = {} does not exist", entry.getValue());
            }
        }
        if (guild.getRoleById(FightBot.configuration.Configuration.getInstance().getActiveStatusRoleId()) == null) {
            log.warn("Active status role with id {} does not exist", FightBot.configuration.Configuration.getInstance().getActiveStatusRoleId());
        }
        log.info("Roles check up has ended");
    }

    public void checkThresholdsOnCall() {
        log.info("Start fighters thresholds check");
        for (Map.Entry<Long, Fighter> entry : fighters.entrySet()) {
            if (entry.getValue().getThreshold() == null) {
                log.warn("Change threshold from null to 0 on {}", entry.getValue());
                entry.getValue().setThreshold(0L);
            }
        }
        log.info("Fighters thresholds check has ended");
    }

    public void checkMembersOnCall() {
        log.info("Start members check up");
        Guild guild = manager.getGuildById(FightBot.configuration.Configuration.getInstance().getGuildId());
        Iterator<Map.Entry<Long, Fighter>> iterator = fighters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Fighter> entry = iterator.next();
            if (guild.getMemberById(entry.getKey()) == null) {
                iterator.remove();
                log.info("Fighter with ID = {} and discord name = {} has been removed", entry.getKey(), entry.getValue().getDiscordName());
            }
            else if (!guild.getMemberById(entry.getKey()).getEffectiveName().isEmpty()
                    && !guild.getMemberById(entry.getKey()).getEffectiveName().equals(entry.getValue().getDiscordName())) {
                entry.getValue().setDiscordName(guild.getMemberById(entry.getKey()).getEffectiveName());
            }
        }
        log.info("Members check up has ended");
    }

    @PostConstruct
    private void init() throws IOException {
        instance = this;
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());
        log.info("<------------ INIT START ------------>");
        if (fightMapFile.exists() && fightMapFile.length() > 0) {
            log.info("Getting fight messages history from text file on Startup");
            TypeReference<Map<Long, FightMessage>> ref = new TypeReference<>() {};
            fightMessages = mapper.readValue(fightMapFile, ref);
        }
        else {
            log.info("File with fight messages history is empty or not present");
        }
        if (lockedFightersFile.exists() && lockedFightersFile.length() > 0) {
            log.info("Getting locked fighters history from text file on Startup");
            TypeReference<List<Long>> ref = new TypeReference<>() {};
            lockedFightersList = mapper.readValue(lockedFightersFile, ref);
        }
        else {
            log.info("File with locked fighters history is empty or not present");
        }
        if (fightersFile.exists() && fightersFile.length() > 0) {
            log.info("Getting fighters history from text file on Startup");
            TypeReference<Map<Long, Fighter>> ref = new TypeReference<>() {};
            fighters = mapper.readValue(fightersFile, ref);
        }
        else {
            log.info("File with fighters history is empty or not present");
        }
        if (fightDatesFile.exists() && fightDatesFile.length() > 0) {
            log.info("Getting fight dates history from text file on Startup");
            TypeReference<List<FightDateLock>> ref = new TypeReference<>() {};
            fightDatesList = mapper.readValue(fightDatesFile, ref);
        }
        else {
            log.info("File with fight dates history is empty or not present");
        }
        if (refereeListFile.exists() && refereeListFile.length() > 0) {
            log.info("Getting referees from text file on Startup");
            TypeReference<List<Long>> ref = new TypeReference<>() {};
            FightBot.configuration.Configuration.getInstance().setReferees(mapper.readValue(refereeListFile, ref));
        }
        else {
            log.info("File with referees is empty or not present");
        }
        log.info("<------------ INIT END -------------->");
    }

    @PreDestroy
    private void destroy() throws IOException {
        saveHistory();
    }

}
