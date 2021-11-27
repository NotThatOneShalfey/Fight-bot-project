package FightBot.buttons;

import FightBot.configuration.Configuration;
import FightBot.entities.Fighter;
import FightBot.interfaces.IButtonCommand;
import FightBot.utils.Constants;
import FightBot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ButtonRank implements IButtonCommand {
    @Override
    public void handle(String arg, ButtonClickEvent event) {
        PrivateChannel textChannel = event.getPrivateChannel();

        Map<Long, Fighter> fighters = Utils.getInstance().fighters;

        Fighter fighter;
        if (fighters.get(event.getUser().getIdLong()) == null) {
            textChannel.sendMessage(String.format(Constants.ON_NON_EXISTING_FIGHTER_REGISTER, event.getUser().getAsMention())).queue(
                    (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
            );
            return;
        }
        fighter = fighters.get(event.getUser().getIdLong());

        textChannel.sendMessage("Карточка :")
                .setEmbeds(fighter.buildEmbed())
                .queue(
                        (message) -> message.delete().queueAfter(15L, TimeUnit.SECONDS)
                );
    }

    @Override
    public String getInvoke() {
        return "Ранг";
    }
}
