package FightBot.utils;

import FightBot.entities.FightMessage;
import FightBot.entities.Fighter;
import FightBot.interfaces.ICommand;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
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

@Slf4j
@Configuration
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

    public Map<Long, Fighter> fighters = new HashMap<>();
    public Map<Long, FightMessage> fightMessages = new HashMap<>();
    public List<Map.Entry<LocalDate, Map.Entry<Long, Long>>> fightDatesList = new ArrayList<>();

    @Setter
    public List<Long> lockedFightersList = new ArrayList<>();

    private static final Random RANDOM = new Random();

    public static Color getRandomColor() {
        float r = RANDOM.nextFloat();
        float g = RANDOM.nextFloat();
        float b = RANDOM.nextFloat();

        return new Color(r, g, b);
    }

    public void saveHistory() throws IOException {
        generateFile(mapper.writeValueAsString(fightMessages), fightMapFile);
        generateFile(mapper.writeValueAsString(lockedFightersList), lockedFightersFile);
        generateFile(mapper.writeValueAsString(fighters), fightersFile);
        generateFile(mapper.writeValueAsString(fightDatesList), fightDatesFile);
    }

    private void generateFile(String str, File file) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        writer.write(str);
        writer.flush();
        writer.close();
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
            TypeReference<List<Map.Entry<LocalDate, Map.Entry<Long, Long>>>> ref = new TypeReference<>() {};
            fightDatesList = mapper.readValue(fightDatesFile, ref);
        }
        else {
            log.info("File with fight dates history is empty or not present");
        }
        log.info("<------------ INIT END -------------->");
    }

    @PreDestroy
    private void destroy() throws IOException {
        saveHistory();
    }

}
