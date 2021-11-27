package FightBot.buttons;

import FightBot.configuration.Configuration;
import FightBot.entities.Fighter;
import FightBot.interfaces.IButtonCommand;
import FightBot.utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.util.concurrent.TimeUnit;

public class ButtonActive implements IButtonCommand {
    @Override
    public void handle(String arg, ButtonClickEvent event) {
        PrivateChannel textChannel = event.getPrivateChannel();

        Guild guild = Utils.getInstance().getManager().getGuildById(Configuration.getInstance().getGuildId());

        Fighter fighter = Utils.getInstance().fighters.get(event.getUser().getIdLong());

        if (fighter != null && !fighter.isActive()) {
            fighter.setActive(true);
            Utils.getInstance().fighters.put(fighter.getId(), fighter);
            guild.addRoleToMember(fighter.getId(), guild.getRoleById(Configuration.getInstance().getActiveStatusRoleId())).queue();
            textChannel.sendMessage(fighter.getDiscordName() + ", вы находитесь в активном поиске противника.").queue((message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
        }

    }

    @Override
    public String getInvoke() {
        return "Активный";
    }
}
