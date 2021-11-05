package FightBot.commands;

import FightBot.configuration.Configuration;
import FightBot.entities.Fighter;
import FightBot.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;

import java.util.Map;

@Slf4j
public class OnRolesChangeHandler {
    private Map<Long, Fighter> fighters = Utils.getInstance().fighters;

    public void handleRolesRemove(GuildMemberRoleRemoveEvent event) {
        // Проверка была ли выдана роль зареганному бойцу
        if (!fighters.containsKey(event.getMember().getIdLong())) {
            return;
        }
        log.info("OnRolesRemove : {} to {}", event.getRoles(), event.getMember());
        Fighter fighter = fighters.get(event.getMember().getIdLong());

        for (Role role : event.getRoles()) {
            if (Configuration.getInstance().titlesList.contains(role.getIdLong()) && fighter.getTitles().contains(role.getName())) {
                fighter.getTitles().remove(role.getName());
            }
        }
        fighters.put(fighter.getId(), fighter);
    }

    public void handleRolesAdd(GuildMemberRoleAddEvent event) {

        // Проверка была ли выдана роль зареганному бойцу
        if (!fighters.containsKey(event.getMember().getIdLong())) {
            return;
        }
        log.info("OnRolesAdd : {} to {}", event.getRoles(), event.getMember());
        Fighter fighter = fighters.get(event.getMember().getIdLong());

        for (Role role : event.getRoles()){
            if (Configuration.getInstance().titlesList.contains(role.getIdLong())) {
                fighter.getTitles().add(role.getName());
            }
        }
        fighters.put(fighter.getId(), fighter);
    }

}
