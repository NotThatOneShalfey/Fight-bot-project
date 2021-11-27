package FightBot.commands;

import FightBot.entities.Fighter;
import FightBot.interfaces.ICommand;
import FightBot.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class TopCommand implements ICommand {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        TextChannel textChannel = event.getChannel();
        Member bot = event.getGuild().getSelfMember();

        // Удаление сообщения команды
        if (bot.hasPermission(Permission.MESSAGE_MANAGE)) {
            event.getMessage().delete().queueAfter(5L, TimeUnit.SECONDS);
        }

        // Берем список всех бойцов
        List<Fighter> fighters = new ArrayList<>(Utils.getInstance().fighters.values());
        // Сортируем
        Collections.sort(fighters);

        // Если аргументы не пустые
        if (!args.isEmpty()) {
            // Сортируем по первому аргументу без учета регистра
            fighters = fighters.stream()
                    .filter(fighter -> fighter.getClasses().stream().map(String::toLowerCase).collect(Collectors.toList())
                                .contains(args.get(0).toLowerCase()))
                    .collect(Collectors.toList());
        }

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
    public String getHelp() {
        return "Команда выводит топ 10 бойцов, от самого высокого ранга. " +
                "Если после команды указать название класса, то выводится рейтинг среди игроков на этом классе.";
    }

    @Override
    public String getInvoke() {
        return "top";
    }

    @Override
    public String getLocalInvoke() {
        return "топ";
    }

    @Override
    public boolean isVisible() {
        return true;
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
