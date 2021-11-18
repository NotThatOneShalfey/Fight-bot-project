package FightBot.commands;

import FightBot.configuration.Configuration;
import FightBot.entities.Fighter;
import FightBot.interfaces.ICommand;
import FightBot.utils.Constants;
import FightBot.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AvailableCommand implements ICommand {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        TextChannel textChannel = event.getChannel();
        Member bot = event.getGuild().getSelfMember();
        // Удаление сообщения команды
        if (bot.hasPermission(Permission.MESSAGE_MANAGE)) {
            event.getMessage().delete().queueAfter(5L, TimeUnit.SECONDS);
        }

        Map<Long, Fighter> fighters = Utils.getInstance().fighters;
        Fighter fighter = fighters.get(event.getMember().getIdLong());

        // Проверка зарегистрирован ли боец
        if (fighter == null) {
            textChannel.sendMessage(String.format(Constants.ON_NON_EXISTING_FIGHTER_REGISTER, event.getAuthor().getAsMention())).queue(
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
            if (event.getGuild().getMemberById(fighterTo.getId()) != null
                    && !event.getGuild().getMemberById(fighterTo.getId()).getOnlineStatus().equals(OnlineStatus.OFFLINE)) {
                availableFightersNames.add(fighterTo.getDiscordName());
                availableFightersClasses.add(String.join(", ", fighterTo.getClasses()));
                availableFightersRanks.add(fighterTo.getRankName());
            }
        }

        if (!availableFightersNames.isEmpty()) {
            textChannel.sendMessageEmbeds(buildEmbed(String.join("\n", availableFightersNames)
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
    public String getHelp() {
        return "Команда возвращает список доступных вам для вызова бойцов."
                + " Боец считается доступным, если боец находится в онлайне, " +
                "а также если его ранг не ниже вашего или выше вашего не более чем на "
                + Configuration.getInstance().getRankDifference() + ".";
    }

    @Override
    public String getInvoke() {
        return "available";
    }

    @Override
    public String getLocalInvoke() {
        return "таблица";
    }

    @Override
    public boolean isVisible() {
        return true;
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
