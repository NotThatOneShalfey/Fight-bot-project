package FightBot.commands;

import FightBot.entities.Fighter;
import FightBot.interfaces.ICommand;
import FightBot.utils.Constants;
import FightBot.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RankCommand implements ICommand {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        TextChannel textChannel = event.getChannel();
        Member bot = event.getGuild().getSelfMember();
        List<User> mentionedUsers = event.getMessage().getMentionedUsers();
        log.info(mentionedUsers.toString());
        Map<Long, Fighter> fighters = Utils.getInstance().fighters;
        // Удаление сообщения команды
        if (bot.hasPermission(Permission.MESSAGE_MANAGE)) {
            event.getMessage().delete().queueAfter(5L, TimeUnit.SECONDS);
        }

        Fighter fighter;
        if (!mentionedUsers.isEmpty()) {
            Long mentionedId = mentionedUsers.get(0).getIdLong();
            if (fighters.get(mentionedId) == null) {
                textChannel.sendMessage(String.format(Constants.ON_SOMEONE_NON_EXISTING_REGISTER, event.getGuild().getMemberById(mentionedUsers.get(0).getIdLong()).getAsMention())).queue(
                        (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
                );
                return;
            }
            fighter = fighters.get(mentionedUsers.get(0).getIdLong());
        }
        else {
            if (fighters.get(event.getMember().getIdLong()) == null) {
                textChannel.sendMessage(String.format(Constants.ON_NON_EXISTING_FIGHTER_REGISTER, event.getAuthor().getAsMention())).queue(
                        (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
                );
                return;
            }
            fighter = fighters.get(event.getMember().getIdLong());
        }
        log.info(fighter.toString());
        textChannel.sendMessage("Карточка :")
                .setEmbeds(fighter.buildEmbed())
                .queue(
                        (message) -> message.delete().queueAfter(15L, TimeUnit.SECONDS)
                );
    }

    @Override
    public String getHelp() {
        return "Команда возвращает полную информацию о вашей статистике.\n" +
                "Команда может принимать в качестве параметра/аргумента упоминание игрока," +
                " по которому вы хотите получить информацию.\nНапример : -" +
                getInvoke() + "/-" + getLocalInvoke() + " @<имя в дискорде>.";
    }

    @Override
    public String getInvoke() {
        return "rank";
    }

    @Override
    public String getLocalInvoke() {
        return "ранг";
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
