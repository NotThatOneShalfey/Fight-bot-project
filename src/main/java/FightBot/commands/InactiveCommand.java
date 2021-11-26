package FightBot.commands;

import FightBot.configuration.Configuration;
import FightBot.entities.Fighter;
import FightBot.interfaces.ICommand;
import FightBot.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class InactiveCommand implements ICommand {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        TextChannel textChannel = event.getChannel();
        Member bot = event.getGuild().getSelfMember();
        List<User> mentionedUsers = event.getMessage().getMentionedUsers();

        // Удаление сообщения команды
        if (bot.hasPermission(Permission.MESSAGE_MANAGE)) {
            event.getMessage().delete().queueAfter(5L, TimeUnit.SECONDS);
        }

        Fighter fighter = Utils.getInstance().fighters.get(event.getMember().getIdLong());

        if (fighter != null && fighter.isActive() && mentionedUsers.isEmpty()) {
            fighter.setActive(false);
            Utils.getInstance().fighters.put(fighter.getId(), fighter);
            event.getGuild().removeRoleFromMember(fighter.getId(), event.getGuild().getRoleById(Configuration.getInstance().getActiveStatusRoleId())).queue();
            textChannel.sendMessage(fighter.getDiscordName() + ", вы исключены из активного поиска.").queue((message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
        }

        // Если вызывает рефери
        if (event.getMember().getIdLong() == Configuration.getInstance().getOwnerId()
                || Configuration.getInstance().referees.contains(event.getMember().getIdLong())) {
            if (!mentionedUsers.isEmpty()) {
                fighter = Utils.getInstance().fighters.get(mentionedUsers.get(0).getIdLong());
                fighter.setActive(false);
                Utils.getInstance().fighters.put(fighter.getId(), fighter);
                event.getGuild().removeRoleFromMember(fighter.getId(), event.getGuild().getRoleById(Configuration.getInstance().getActiveStatusRoleId())).queue();
                log.info("Active role removed from {} by referee {}", fighter.getDiscordName(), event.getMember().getEffectiveName());
            }
        }

    }

    @Override
    public String getHelp() {
        return "Команда позволяет установить отметку о том, что боец не готов к бою и не хочет появляться в списке доступных для вызова бойцов.";
    }

    @Override
    public String getInvoke() {
        return "inactive";
    }

    @Override
    public String getLocalInvoke() {
        return "неактивен";
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
