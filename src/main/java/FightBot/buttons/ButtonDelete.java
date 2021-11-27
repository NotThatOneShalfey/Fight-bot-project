package FightBot.buttons;

import FightBot.interfaces.IButtonCommand;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class ButtonDelete implements IButtonCommand {
    @Override
    public void handle(String arg, ButtonClickEvent event) {
        event.getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "Удалить";
    }
}
