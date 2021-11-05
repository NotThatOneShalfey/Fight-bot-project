package FightBot.interfaces;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public interface IButtonCommand {
    void handle(String arg, ButtonClickEvent event);

    String getInvoke();
}
