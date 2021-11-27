package FightBot.buttons;

import FightBot.configuration.Configuration;
import FightBot.entities.Fighter;
import FightBot.interfaces.IButtonCommand;
import FightBot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ButtonTop implements IButtonCommand {
    @Override
    public void handle(String arg, ButtonClickEvent event) {
        PrivateChannel textChannel = event.getPrivateChannel();

        // Берем список всех бойцов
        List<Fighter> fighters = new ArrayList<>(Utils.getInstance().fighters.values());
        // Сортируем
        Collections.sort(fighters);

        // Обрубаем первые 10
        int maxIndx = Math.min(fighters.size(), 10);
        fighters = new ArrayList<>(fighters.subList(0, maxIndx));

        List<String> fightersNames = new ArrayList<>();
        List<String> fightersRanks = new ArrayList<>();
        List<String> fightersClasses = new ArrayList<>();

        // Проходимся по всему списку
        for (Fighter fighter : fighters) {
            // Если среди всех классов
            fightersNames.add(fighter.getDiscordName());
            fightersRanks.add(fighter.getRankName());
            fightersClasses.add(String.join(", ", fighter.getClasses()));
        }
        // Выкидываем эмбед
        if (!fightersNames.isEmpty()) {
            textChannel.sendMessageEmbeds(buildEmbed(String.join("\n", fightersNames)
                            , String.join("\n", fightersRanks)
                            , String.join("\n", fightersClasses)))
                    .queue(
                            (message) -> message.delete().queueAfter(40L, TimeUnit.SECONDS)
                    );
        }
    }

    @Override
    public String getInvoke() {
        return "Топ";
    }

    private MessageEmbed buildEmbed(String names, String ranks, String classes) {
        EmbedBuilder eBuilder = new EmbedBuilder();
        eBuilder.setTitle("Топ бойцов")
                .addField("Имя в дискорде", names, true)
                .addField("Классы", classes, true)
                .addField("Ранг", ranks, true);
        return eBuilder.build();
    }
}
