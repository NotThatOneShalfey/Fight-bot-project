package FightBot.commands;

import FightBot.entities.Fighter;
import FightBot.interfaces.ISelectionMenuCommand;
import FightBot.utils.Constants;
import FightBot.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ClassSelection implements ISelectionMenuCommand {

    @Override
    public void handle(SelectionMenuEvent event) {
        PrivateChannel channel = event.getUser().openPrivateChannel().complete();
        // Удаление сообщения команды
        event.getMessage().delete().queue();

        Fighter fighter = Utils.getInstance().fighters.get(event.getUser().getIdLong());
        fighter.getClasses().clear();
        for (SelectOption option : event.getSelectedOptions()) {
            fighter.getClasses().add(option.getLabel());
        }

        channel.sendMessage("Ваша карточка :")
                .setEmbeds(fighter.buildEmbed())
                .queue(
                        (message) -> message.delete().queueAfter(15L, TimeUnit.SECONDS)
                );
    }

    @Override
    public String getInvoke() {
        return Constants.SELECTION_CLASS_ID;
    }
}
