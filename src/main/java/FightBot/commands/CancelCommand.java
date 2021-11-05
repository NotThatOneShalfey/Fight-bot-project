package FightBot.commands;

import FightBot.configuration.Configuration;
import FightBot.entities.FightMessage;
import FightBot.entities.Fighter;
import FightBot.interfaces.ICommand;
import FightBot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CancelCommand implements ICommand {

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        Member bot = event.getGuild().getSelfMember();

        // Если запрашивает не рефери
        if (!Configuration.getInstance().getReferees().contains(event.getMember().getIdLong())) {
            return;
        }

        // Удаление сообщения команды
        if (bot.hasPermission(Permission.MESSAGE_MANAGE)) {
            event.getMessage().delete().queueAfter(5L, TimeUnit.SECONDS);
        }
        // Если аргументы пустые
        if (args.isEmpty()) {
            return;
        }

        Long fightId = Long.parseLong(args.get(0));

        FightMessage fightMessage = Utils.getInstance().fightMessages.get(fightId);
        Fighter firstFighter = fightMessage.getFirstFighter();
        Fighter secondFighter = fightMessage.getSecondFighter();

        Utils.getInstance().fightMessages.remove(fightId);
        Utils.getInstance().lockedFightersList.remove(firstFighter.getId());
        Utils.getInstance().lockedFightersList.remove(secondFighter.getId());

        Utils.getInstance().getManager()
                .getGuildById(Configuration.getInstance().getGuildId())
                .getTextChannelById(Configuration.getInstance().getHistoryChannelId())
                .retrieveMessageById(fightId)
                .complete()
                .delete()
                .queue();
    }

    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public String getInvoke() {
        return "cancel";
    }

    @Override
    public String getLocalInvoke() {
        return "отмена";
    }

    @Override
    public boolean isVisible() {
        return false;
    }
}
