package FightBot.interfaces;

import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;

public interface ISelectionMenuCommand {
    void handle(SelectionMenuEvent event);

    String getInvoke();
}
