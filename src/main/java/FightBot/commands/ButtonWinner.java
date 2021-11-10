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
import net.dv8tion.jda.api.entities.Role;
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
        // Проверка, если на кнопку нажимает рефери
        if (!Configuration.getInstance().getReferees().contains(event.getMember().getIdLong())) {
            // Проверка, если кнопку нажали игроки
            if (event.getMember().getIdLong() != firstFighter.getId() && event.getMember().getIdLong() != secondFighter.getId()) {
                return;
            }

            // Записываем результат от первого бойца
            if (event.getMember().getIdLong() == firstFighter.getId()) {
                fightMessage.setFirstFighterWinnerDecision(event.getComponent().getLabel());
            }
            // Записываем результат от второго бойца
            else {
                fightMessage.setSecondFighterWinnerDecision(event.getComponent().getLabel());
            }

            // Редактируем с учетом того, кто нажал кнопку
            event.getMessage()
                    .editMessageEmbeds(fightMessage.buildEmbed())
                    .queue();

            // Сохраняем сообщение
            Utils.getInstance().fightMessages.put(event.getMessage().getIdLong(), fightMessage);

            // Если результат игроков не сошёлся, ждем дальше
            if (!fightMessage.getFirstFighterWinnerDecision().equals(fightMessage.getSecondFighterWinnerDecision())) {
                return;
            }
        }
        // Если результат сошёлся или выбрал рефери
        Map<Long, Long> rankingsMap = Configuration.getInstance().rankingsMap;

        Long firstMemberNextRank = firstFighter.getRank();
        Long secondMemberNextRank = secondFighter.getRank();

        Boolean highBid = false;

        if (Math.max(firstMemberNextRank, secondMemberNextRank) - Math.min(firstMemberNextRank, secondMemberNextRank) > 3) {
            highBid = true;
        }
        log.debug("High bid : {}", highBid);

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
        // Если победил первый
        if (event.getComponent().getLabel().contains(firstFighter.getDiscordName())) {
            // Если ранг первого максимальный, то оставляем без изменений
            if (rankingsMap.get(firstMemberNextRank + 1) == null) {
                firstMemberNextRankId = firstMemberCurrentRankId;
            }
            // Если высокие ставки и ранг первого меньше ранга второго, то добавляем +2
            else if (highBid && firstMemberNextRank < secondMemberNextRank && rankingsMap.get(firstMemberNextRank + 2) != null) {
                firstMemberNextRank = firstMemberNextRank + 2;
                firstMemberNextRankId = rankingsMap.get(firstMemberNextRank);
            }
            // В другом случае добавляем +1
            else {
                firstMemberNextRank = firstMemberNextRank + 1;
                firstMemberNextRankId = rankingsMap.get(firstMemberNextRank);
            }
            // Если ранг второго минимальный, то оставляем без изменений
            if (rankingsMap.get(secondMemberNextRank - 1) == null) {
                secondMemberNextRankId = secondMemberCurrentRankId;
            }
            // Если высокие ставки и ранг первого меньше ранга второго, то понижаем на -2
            else if (highBid && firstMemberNextRank < secondMemberNextRank && rankingsMap.get(secondMemberNextRank - 2) != null) {
                secondMemberNextRank = secondMemberNextRank - 2;
                secondMemberNextRankId = rankingsMap.get(secondMemberNextRank);
            }
            // В другом случае понижаем -1
            else {
                secondMemberNextRank = secondMemberNextRank - 1;
                secondMemberNextRankId = rankingsMap.get(secondMemberNextRank);
            }
            // Устанавливаем победы/поражения
            firstFighter.setWins(firstFighterWins + 1);
            firstFighter.setWinStreak(firstFighterWinStreak + 1);
            secondFighter.setLoses(secondFighterLoses + 1);
            if (secondFighterWinStreak != 0) {
                secondFighter.setWinStreak(0L);
            }
        }
        // Если победил второй
        else {
            // Если ранг первого минимальный, то оставляем без изменений
            if (rankingsMap.get(firstMemberNextRank - 1) == null) {
                firstMemberNextRankId = firstMemberCurrentRankId;
            }
            // Если высокие ставки и ранг второго меньше ранга первого, то понижаем на -2
            else if (highBid && secondMemberNextRank < firstMemberNextRank && rankingsMap.get(firstMemberNextRank - 2) != null) {
                firstMemberNextRank = firstMemberNextRank - 2;
                firstMemberNextRankId = rankingsMap.get(firstMemberNextRank);
            }
            // В другом случае понижаем -1
            else {
                firstMemberNextRank = firstMemberNextRank - 1;
                firstMemberNextRankId = rankingsMap.get(firstMemberNextRank);
            }
            // Если ранг второго максимальный, то оставляем без изменений
            if (rankingsMap.get(secondMemberNextRank + 1) == null) {
                secondMemberNextRankId = secondMemberCurrentRankId;
            }
            // Если высокие ставки и ранг второго меньше ранга первого, то повышаем на +2
            else if (highBid && secondMemberNextRank < firstMemberNextRank && rankingsMap.get(secondMemberNextRank + 2) != null) {
                secondMemberNextRank = secondMemberNextRank + 2;
                secondMemberNextRankId = rankingsMap.get(secondMemberNextRank);
            }
            // В другом случае повышаем +1
            else {
                secondMemberNextRank = secondMemberNextRank + 1;
                secondMemberNextRankId = rankingsMap.get(secondMemberNextRank);
            }
            // Устанавливаем победы/поражения
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

        // Титул "Сизиф"
        if (!Configuration.getInstance().isInDebugMode()) {
            // Получение роли
            Role title = guild.getRoleById(906167897532018768L);
            // Проверяем победы поражения первого игрока
            if (firstFighter.getWins() < firstFighter.getLoses() && !guild.getMemberById(firstFighter.getId()).getRoles().contains(title)) {
                guild.addRoleToMember(firstFighter.getId(), title).queue();
            }
            else if (firstFighter.getWins() >= firstFighter.getLoses() && guild.getMemberById(firstFighter.getId()).getRoles().contains(title)) {
                guild.removeRoleFromMember(firstFighter.getId(), title).queue();
            }
            // Проверяем победы поражения второго игрока
            if (secondFighter.getWins() < secondFighter.getLoses() && !guild.getMemberById(secondFighter.getId()).getRoles().contains(title)) {
                guild.addRoleToMember(secondFighter.getId(), title).queue();
            }
            else if (secondFighter.getWins() >= secondFighter.getLoses() && guild.getMemberById(secondFighter.getId()).getRoles().contains(title)) {
                guild.removeRoleFromMember(secondFighter.getId(), title).queue();
            }
        }

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
