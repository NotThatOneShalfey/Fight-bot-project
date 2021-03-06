package FightBot.commands;

import FightBot.configuration.Configuration;
import FightBot.entities.Fighter;
import FightBot.interfaces.ICommand;
import FightBot.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ActiveCommand implements ICommand {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        TextChannel textChannel = event.getChannel();
        Member bot = event.getGuild().getSelfMember();

        // Удаление сообщения команды
        if (bot.hasPermission(Permission.MESSAGE_MANAGE)) {
            event.getMessage().delete().queueAfter(5L, TimeUnit.SECONDS);
        }

        Fighter fighter = Utils.getInstance().fighters.get(event.getMember().getIdLong());

        if (fighter != null && !fighter.isActive()) {
            fighter.setActive(true);
            Utils.getInstance().fighters.put(fighter.getId(), fighter);
            event.getGuild().addRoleToMember(fighter.getId(), event.getGuild().getRoleById(Configuration.getInstance().getActiveStatusRoleId())).queue();
            textChannel.sendMessage(fighter.getDiscordName() + ", вы находитесь в активном поиске противника.").queue((message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
        }

    }

    @Override
    public String getHelp() {
        return "Команда позволяет установить отметку о том, что боец готов к бою и хочет появляться в списке доступных бойцов для вызова.";
    }

    @Override
    public String getInvoke() {
        return "active";
    }

    @Override
    public String getLocalInvoke() {
        return "активен";
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
