package FightBot.commands;

import FightBot.configuration.Configuration;
import FightBot.entities.Fighter;
import FightBot.utils.Constants;
import FightBot.utils.Utils;
import FightBot.entities.FightMessage;
import FightBot.interfaces.IButtonCommand;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
public class ButtonWinner implements IButtonCommand {
    @Override
    public void handle(String arg, ButtonClickEvent event) {
        FightMessage fightMessage = Utils.getInstance().fightMessages.get(event.getMessage().getIdLong());
        Fighter firstFighter = fightMessage.getFirstFighter();
        Fighter secondFighter = fightMessage.getSecondFighter();
        Guild guild = event.getGuild();
        Member bot = guild.getSelfMember();

        if (!bot.hasPermission(Permission.MANAGE_ROLES)) {
            return;
        }

        if (!Configuration.getInstance().getReferees().contains(event.getMember().getIdLong())) {
            if (event.getMember().getIdLong() != firstFighter.getId() && event.getMember().getIdLong() != secondFighter.getId()) {
                return;
            }

            if (event.getMember().getIdLong() == firstFighter.getId()) {
                fightMessage.setFirstFighterWinnerDecision(event.getComponent().getLabel());
            } else {
                fightMessage.setSecondFighterWinnerDecision(event.getComponent().getLabel());
            }

            event.getMessage()
                    .editMessageEmbeds(fightMessage.buildEmbed())
                    .queue();

            Utils.getInstance().fightMessages.put(event.getMessage().getIdLong(), fightMessage);

            if (!fightMessage.getFirstFighterWinnerDecision().equals(fightMessage.getSecondFighterWinnerDecision())) {
                return;
            }
        }

        Map<Long, Long> rankingsMap = Configuration.getInstance().rankingsMap;

        Long firstMemberNextRank = firstFighter.getRank();
        Long secondMemberNextRank = secondFighter.getRank();

        Long firstMemberCurrentRankId = rankingsMap.get(firstMemberNextRank);
        Long secondMemberCurrentRankId = rankingsMap.get(secondMemberNextRank);

        Long firstMemberNextRankId;
        Long secondMemberNextRankId;

        Long firstFighterWins = firstFighter.getWins();
        Long firstFighterWinStreak = firstFighter.getWinStreak();
        Long firstFighterLoses = firstFighter.getLoses();
        Long secondFighterWins = secondFighter.getWins();
        Long secondFighterWinStreak = secondFighter.getWinStreak();
        Long secondFighterLoses = secondFighter.getLoses();

        // Вычисляем кто выиграл по нажатой кнопке
        if (event.getComponent().getLabel().contains(firstFighter.getDiscordName())) {
            if (rankingsMap.get(firstMemberNextRank + 1) == null) {
                firstMemberNextRankId = firstMemberCurrentRankId;
            }
            else {
                firstMemberNextRank = firstMemberNextRank + 1;
                firstMemberNextRankId = rankingsMap.get(firstMemberNextRank);
            }
            if (rankingsMap.get(secondMemberNextRank - 1) == null) {
                secondMemberNextRankId = secondMemberCurrentRankId;
            }
            else {
                secondMemberNextRank = secondMemberNextRank - 1;
                secondMemberNextRankId = rankingsMap.get(secondMemberNextRank);
            }
            firstFighter.setWins(firstFighterWins + 1);
            firstFighter.setWinStreak(firstFighterWinStreak + 1);
            secondFighter.setLoses(secondFighterLoses + 1);
            if (secondFighterWinStreak != 0) {
                secondFighter.setWinStreak(0L);
            }
        }
        else {
            if (rankingsMap.get(firstMemberNextRank - 1) == null) {
                firstMemberNextRankId = firstMemberCurrentRankId;
            }
            else {
                firstMemberNextRank = firstMemberNextRank - 1;
                firstMemberNextRankId = rankingsMap.get(firstMemberNextRank);
            }
            if (rankingsMap.get(secondMemberNextRank + 1) == null) {
                secondMemberNextRankId = secondMemberCurrentRankId;
            }
            else {
                secondMemberNextRank = secondMemberNextRank + 1;
                secondMemberNextRankId = rankingsMap.get(secondMemberNextRank);
            }
            secondFighter.setWins(secondFighterWins + 1);
            secondFighter.setWinStreak(secondFighterWinStreak + 1);
            firstFighter.setLoses(firstFighterLoses + 1);
            if (firstFighterWinStreak != 0) {
                firstFighter.setWinStreak(0L);
            }
        }
        log.debug("Отметка о рангах : " +
                        "Текущий ранг 1го бойца = {}. " +
                        "Текущий ранг 2го бойца = {}. " +
                        "После изменения ранг 1го бойца = {}. " +
                        "После изменения ранг 2го бойца = {}. "
                , guild.getRoleById(firstMemberCurrentRankId)
                , guild.getRoleById(secondMemberCurrentRankId)
                , guild.getRoleById(firstMemberNextRankId)
                , guild.getRoleById(secondMemberNextRankId));

        // Убираем роль у первого бойца
        guild.removeRoleFromMember(firstFighter.getId(), guild.getRoleById(firstMemberCurrentRankId)).complete();
        // Добавляем роль первому бойцу
        guild.addRoleToMember(firstFighter.getId(), guild.getRoleById(firstMemberNextRankId)).queue();
        firstFighter.setRank(firstMemberNextRank);
        firstFighter.setRankName(guild.getRoleById(firstMemberNextRankId).getName());
        // Убираем роль у второго бойца
        guild.removeRoleFromMember(secondFighter.getId(), guild.getRoleById(secondMemberCurrentRankId)).complete();
        // Добавляем роль второму бойцу
        guild.addRoleToMember(secondFighter.getId(), guild.getRoleById(secondMemberNextRankId)).queue();
        secondFighter.setRank(secondMemberNextRank);
        secondFighter.setRankName(guild.getRoleById(secondMemberNextRankId).getName());

        fightMessage.setFirstFighter(firstFighter);
        fightMessage.setSecondFighter(secondFighter);
        fightMessage.setHasEnded(true);
        // Выкидываем эмбед
        event.getMessage()
                .editMessage(" ")
                .setEmbeds(fightMessage.buildEmbed())
                .setActionRow(event.getComponent().asDisabled())
                .queue();

        Utils.getInstance().lockedFightersList.remove(firstFighter.getId());
        Utils.getInstance().lockedFightersList.remove(secondFighter.getId());



        Utils.getInstance().fighters.put(firstFighter.getId(), firstFighter);
        Utils.getInstance().fighters.put(secondFighter.getId(), secondFighter);

        log.debug("List of locked fighters on winner button : {}", Utils.getInstance().lockedFightersList.toString());

        Utils.getInstance().fightMessages.remove(event.getMessage().getIdLong());
        Utils.getInstance().fightDatesList.add(Map.entry(LocalDate.now(), Map.entry(firstFighter.getId(), secondFighter.getId())));
    }

    @Override
    public String getInvoke() {
        return Constants.BUTTON_CLAIM_WINNER;
    }
}
