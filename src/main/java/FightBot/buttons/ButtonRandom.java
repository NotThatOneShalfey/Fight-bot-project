package FightBot.buttons;

import FightBot.configuration.Configuration;
import FightBot.entities.Fighter;
import FightBot.interfaces.IButtonCommand;
import FightBot.utils.Constants;
import FightBot.utils.Utils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ButtonRandom implements IButtonCommand {

    @Override
    public void handle(String arg, ButtonClickEvent event) {
        PrivateChannel textChannel = event.getPrivateChannel();

        Guild guild = Utils.getInstance().getManager().getGuildById(Configuration.getInstance().getGuildId());
        
        Fighter fighter = Utils.getInstance().fighters.get(event.getUser().getIdLong());

        if (fighter == null) {
            textChannel.sendMessage(String.format(Constants.ON_NON_EXISTING_FIGHTER_REGISTER, event.getUser().getAsMention())).queue(
                    (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
            );
            return;
        }

        List<Fighter> availableFighters = Utils.getInstance().getListOfAvailableFighters(fighter).stream()
                .filter(fighter1 -> guild.getMemberById(fighter1.getId()) != null
                        && !guild.getMemberById(fighter1.getId()).getOnlineStatus().equals(OnlineStatus.OFFLINE))
                .collect(Collectors.toList());

        if (!availableFighters.isEmpty()) {
            Fighter availableFighter = availableFighters.get(Utils.RANDOM.nextInt(availableFighters.size()));
            textChannel.sendMessage("Попробуйте вызвать - " + availableFighter.getDiscordName())
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
    public String getInvoke() {
        return "Случайно";
    }
}
