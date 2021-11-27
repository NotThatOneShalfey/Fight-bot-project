package FightBot.buttons;

import FightBot.configuration.Configuration;
import FightBot.entities.Fighter;
import FightBot.interfaces.IButtonCommand;
import FightBot.utils.Constants;
import FightBot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ButtonAvailable implements IButtonCommand {

    @Override
    public void handle(String arg, ButtonClickEvent event) {
        PrivateChannel textChannel = event.getPrivateChannel();

        Guild guild = Utils.getInstance().getManager().getGuildById(Configuration.getInstance().getGuildId());

        Map<Long, Fighter> fighters = Utils.getInstance().fighters;
        Fighter fighter = fighters.get(event.getUser().getIdLong());

        // Проверка зарегистрирован ли боец
        if (fighter == null) {
            textChannel.sendMessage(String.format(Constants.ON_NON_EXISTING_FIGHTER_REGISTER, event.getUser().getAsMention())).queue(
                    (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
            );
            return;
        }

        List<String> availableFightersNames = new ArrayList<>();
        List<String> availableFightersRanks = new ArrayList<>();
        List<String> availableFightersClasses = new ArrayList<>();

        // Идем по списку доступных противников
        for (Fighter fighterTo : Utils.getInstance().getListOfAvailableFighters(fighter)) {
            // Проверка статуса в дискорде
            if (guild.getMemberById(fighterTo.getId()) != null
                    && !guild.getMemberById(fighterTo.getId()).getOnlineStatus().equals(OnlineStatus.OFFLINE)) {
                availableFightersNames.add(fighterTo.getDiscordName());
                availableFightersClasses.add(String.join(", ", fighterTo.getClasses()));
                availableFightersRanks.add(fighterTo.getRankName());
            }
        }

        if (!availableFightersNames.isEmpty()) {
            textChannel.sendMessage("Таблица доступных для вас игроков.")
                    .setEmbeds(buildEmbed(String.join("\n", availableFightersNames)
                            , String.join("\n", availableFightersRanks)
                            , String.join("\n", availableFightersClasses)))
                    .queue(
                            (message) -> message.delete().queueAfter(120L, TimeUnit.SECONDS)
                    );
        } else {
            textChannel.sendMessage("Нет доступных бойцов для вызова").queue(
                    (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
            );
        }
    }

    @Override
    public String getInvoke() {
        return "Таблица";
    }

    private MessageEmbed buildEmbed(String names, String ranks, String classes) {
        EmbedBuilder eBuilder = new EmbedBuilder();
        eBuilder.setTitle("Список доступных для вызова бойцов")
                .addField("Имя в дискорде", names, true)
                .addField("Классы", classes, true)
                .addField("Ранг", ranks, true);
        return eBuilder.build();
    }
}
