package FightBot.commands;

import FightBot.configuration.Configuration;
import FightBot.entities.Fighter;
import FightBot.utils.Constants;
import FightBot.utils.Utils;
import FightBot.entities.FightMessage;
import FightBot.interfaces.IButtonCommand;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

@Slf4j
public class ButtonDecline implements IButtonCommand {
    @Override
    public void handle(String arg, ButtonClickEvent event) {
        FightMessage fightMessage = Utils.getInstance().fightMessages.get(event.getMessage().getIdLong());
        Fighter firstFighter = fightMessage.getFirstFighter();
        Fighter secondFighter = fightMessage.getSecondFighter();

        if (event.getMember().getIdLong() == firstFighter.getId()
                || event.getMember().getIdLong() == secondFighter.getId()
                || Configuration.getInstance().getReferees().contains(event.getMember().getIdLong())) {
            Utils.getInstance().fightMessages.remove(event.getMessage().getIdLong());
            Utils.getInstance().lockedFightersList.remove(firstFighter.getId());
            Utils.getInstance().lockedFightersList.remove(secondFighter.getId());

            event.getHook().deleteOriginal().queue();
        }

        log.debug("List of locked fighters on decline button :{}", Utils.getInstance().lockedFightersList.toString());

    }

    @Override
    public String getInvoke() {
        return Constants.BUTTON_DECLINE_COMMAND_ID;
    }
}
