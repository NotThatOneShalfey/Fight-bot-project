package FightBot;

import FightBot.configuration.Configuration;
import FightBot.core.Listener;

import FightBot.utils.Utils;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.EnumSet;

@SpringBootApplication
public class BotApp {
    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(BotApp.class, args);

        EnumSet<GatewayIntent> intents = EnumSet.of(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_BANS,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES
        );

        Listener listener = new Listener();
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.create(Configuration.getInstance().getToken(), intents);
        builder.setActivity(Activity.playing("Black Desert Online"))
                .setStatus(OnlineStatus.ONLINE)
                .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE)
                .addEventListeners(listener);

        Utils.getInstance().setManager(builder.build());
    }
}
