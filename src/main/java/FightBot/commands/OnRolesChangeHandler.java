package FightBot.commands;

import FightBot.configuration.Configuration;
import FightBot.entities.Fighter;
import FightBot.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
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

        // Если удаляется роль рефери удаляем из списка рефери
        if (event.getRoles().contains(event.getGuild().getRoleById(Configuration.getInstance().getRefereeRoleId()))) {
            Configuration.getInstance().referees.remove(event.getMember().getIdLong());
        }

        Fighter fighter = fighters.get(event.getMember().getIdLong());

        for (Role role : event.getRoles()) {
            if (Configuration.getInstance().titlesList.contains(role.getIdLong()) && fighter.getTitles().contains(role.getName())) {
                fighter.getTitles().remove(role.getName());
            }
        }
        fighters.put(fighter.getId(), fighter);
    }

    public void handleRolesAdd(GuildMemberRoleAddEvent event) {
        // Если добавляется роль рефери добавляем в список рефери
        if (event.getRoles().contains(event.getGuild().getRoleById(Configuration.getInstance().getRefereeRoleId()))) {
            Configuration.getInstance().referees.add(event.getMember().getIdLong());
        }
        log.info("OnRolesAdd : {} to {}", event.getRoles(), event.getMember());
        // Проверка была ли выдана роль зареганному бойцу
        if (!fighters.containsKey(event.getMember().getIdLong())) {
            return;
        }
        Fighter fighter = fighters.get(event.getMember().getIdLong());
        handleThreshold(event, fighter);
        for (Role role : event.getRoles()){
            if (Configuration.getInstance().titlesList.contains(role.getIdLong())) {
                fighter.getTitles().add(role.getName());
            }
        }
        fighters.put(fighter.getId(), fighter);
    }

    public void handleThreshold(GuildMemberRoleAddEvent event, Fighter fighter) {
        Member member = event.getMember();

        long thresholdValue = fighter.getRank() / 5;
        Long fighterThresholdId = Configuration.getInstance().thresholdsMap.get(thresholdValue);
        Long fighterPrevThresholdId = Configuration.getInstance().thresholdsMap.get(thresholdValue - 1);
        Long fighterFutThresholdId = Configuration.getInstance().thresholdsMap.get(thresholdValue + 1);
        log.debug("Handle threshold debug, value : {}, currThreshold = {}, prevThreshold = {}", thresholdValue, fighterThresholdId, fighterPrevThresholdId);
        // Задаем новый пороговый ранг
        if (fighterThresholdId != null && !member.getRoles().contains(event.getGuild().getRoleById(fighterThresholdId))) {
            event.getGuild().addRoleToMember(fighter.getId(), event.getGuild().getRoleById(fighterThresholdId)).complete();
            fighter.setThreshold(thresholdValue);
            fighters.put(fighter.getId(), fighter);
            log.debug("Set new threshold role and value to {}", fighter.getDiscordName());
        }
        // Если файтер поднялся удаляем старый
        if (fighterPrevThresholdId != null && member.getRoles().contains(event.getGuild().getRoleById(fighterPrevThresholdId))) {
            event.getGuild().removeRoleFromMember(fighter.getId(), event.getGuild().getRoleById(fighterPrevThresholdId)).complete();
            log.debug("Remove old threshold role on upgrade from {}", fighter.getDiscordName());
        }
        // Если файтер опустился
        if (fighterFutThresholdId != null && member.getRoles().contains(event.getGuild().getRoleById(fighterFutThresholdId))) {
            event.getGuild().removeRoleFromMember(fighter.getId(), event.getGuild().getRoleById(fighterFutThresholdId)).complete();
            log.debug("Remove old threshold role on downgrade from {}", fighter.getDiscordName());
        }
    }

}
