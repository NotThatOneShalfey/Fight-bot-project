package FightBot.commands;

import FightBot.entities.Fighter;
import FightBot.interfaces.ICommand;
import FightBot.utils.Constants;
import FightBot.utils.Utils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RandomCommand implements ICommand {

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        TextChannel textChannel = event.getChannel();
        Member bot = event.getGuild().getSelfMember();

        // Удаление сообщения команды
        if (bot.hasPermission(Permission.MESSAGE_MANAGE)) {
            event.getMessage().delete().queueAfter(5L, TimeUnit.SECONDS);
        }

        Fighter fighter = Utils.getInstance().fighters.get(event.getMember().getIdLong());

        if (fighter == null) {
            textChannel.sendMessage(String.format(Constants.ON_NON_EXISTING_FIGHTER_REGISTER, event.getAuthor().getAsMention())).queue(
                    (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
            );
            return;
        }

        List<Fighter> availableFighters = Utils.getInstance().getListOfAvailableFighters(fighter).stream()
                .filter(fighter1 -> event.getGuild().getMemberById(fighter1.getId()) != null
                        && !event.getGuild().getMemberById(fighter1.getId()).getOnlineStatus().equals(OnlineStatus.OFFLINE))
                .collect(Collectors.toList());

        if (!availableFighters.isEmpty()) {
            Fighter availableFighter = availableFighters.get(Utils.RANDOM.nextInt(availableFighters.size()));
            textChannel.sendMessage(event.getAuthor().getAsMention() + ", попробуйте вызвать - " + availableFighter.getDiscordName())
                    .setEmbeds(availableFighter.buildEmbed())
                    .queue((message) -> message.delete().queueAfter(20L, TimeUnit.SECONDS));
        }
        else {
            textChannel.sendMessage("Нет доступных бойцов для вызова").queue(
                    (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
            );
        }
    }

    @Override
    public String getHelp() {
        return "Команда позволяет получить имя в дискорде случайного противника, который подходит для вас с учетом ранга и готовности к бою.";
    }

    @Override
    public String getInvoke() {
        return "random";
    }

    @Override
    public String getLocalInvoke() {
        return "случайно";
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
