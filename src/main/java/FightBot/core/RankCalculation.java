package FightBot.core;

import FightBot.configuration.Configuration;
import FightBot.entities.Fighter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Slf4j
public class RankCalculation {
    private Guild guild;

    public List<Fighter> onWinnerDecision(Fighter winner, Fighter loser) {
        Map<Long, Long> rankingsMap = Configuration.getInstance().rankingsMap;

        Long winnerNextRank = winner.getRank();
        Long loserNextRank = loser.getRank();

        Boolean highBid = false;

        if (Math.max(winnerNextRank, loserNextRank) - Math.min(winnerNextRank, loserNextRank) > 3) {
            highBid = true;
        }
        log.debug("High bid : {}", highBid);

        Long winnerCurrentRankId = rankingsMap.get(winnerNextRank);
        Long loserCurrentRankId = rankingsMap.get(loserNextRank);

        Long winnerNextRankId;
        Long loserNextRankId;

        Long winnerWins = winner.getWins();
        Long winnerWinStreak = winner.getWinStreak();
        Long loserWinStreak = loser.getWinStreak();
        Long loserLoses = loser.getLoses();

        // Если ранг победителя максимальный, то оставляем без изменений
        if (rankingsMap.get(winnerNextRank + 1) == null) {
            winnerNextRankId = winnerCurrentRankId;
        }
        // Если высокие ставки и ранг победителя меньше проигравшего второго, то добавляем +2
        else if (highBid && winnerNextRank < loserNextRank && rankingsMap.get(winnerNextRank + 2) != null) {
            winnerNextRank = winnerNextRank + 2;
            winnerNextRankId = rankingsMap.get(winnerNextRank);
        }
        // В другом случае добавляем +1
        else {
            winnerNextRank = winnerNextRank + 1;
            winnerNextRankId = rankingsMap.get(winnerNextRank);
        }
        // Если ранг проигравшего минимальный, то оставляем без изменений
        if (rankingsMap.get(loserNextRank - 1) == null) {
            loserNextRankId = loserCurrentRankId;
        }
        // Если высокие ставки и ранг победителя меньше ранга проигравшего, то понижаем на -2
        else if (highBid && winnerNextRank < loserNextRank && rankingsMap.get(loserNextRank - 2) != null) {
            loserNextRank = loserNextRank - 2;
            loserNextRankId = rankingsMap.get(loserNextRank);
        }
        // В другом случае понижаем -1
        else {
            loserNextRank = loserNextRank - 1;
            loserNextRankId = rankingsMap.get(loserNextRank);
        }
        // Устанавливаем победы/поражения
        winner.setWins(winnerWins + 1);
        winner.setWinStreak(winnerWinStreak + 1);
        loser.setLoses(loserLoses + 1);
        if (loserWinStreak != 0) {
            loser.setWinStreak(0L);
        }

        // Титул "Сизиф"
        checkTitleSyzif(winner);
        checkTitleSyzif(loser);

        // Убираем роль у первого бойца
        guild.removeRoleFromMember(winner.getId(), guild.getRoleById(winnerCurrentRankId)).complete();
        // Добавляем роль первому бойцу
        guild.addRoleToMember(winner.getId(), guild.getRoleById(winnerNextRankId)).queue();
        winner.setRank(winnerNextRank);
        winner.setRankName(guild.getRoleById(winnerNextRankId).getName());
        // Убираем роль у второго бойца
        guild.removeRoleFromMember(loser.getId(), guild.getRoleById(loserCurrentRankId)).complete();
        // Добавляем роль второму бойцу
        guild.addRoleToMember(loser.getId(), guild.getRoleById(loserNextRankId)).queue();
        loser.setRank(loserNextRank);
        loser.setRankName(guild.getRoleById(loserNextRankId).getName());

        List<Fighter> fighterList = new LinkedList<>();
        fighterList.add(winner);
        fighterList.add(loser);

        return fighterList;
    }

    private void checkTitleSyzif(Fighter fighter) {
        // Получение роли
        Role title = guild.getRoleById(Configuration.getInstance().getSyzifRankId());
        // Проверяем победы поражения первого игрока
        if (fighter.getWins() < fighter.getLoses() && !guild.getMemberById(fighter.getId()).getRoles().contains(title)) {
            guild.addRoleToMember(fighter.getId(), title).queue();
        }
        else if (fighter.getWins() >= fighter.getLoses() && guild.getMemberById(fighter.getId()).getRoles().contains(title)) {
            guild.removeRoleFromMember(fighter.getId(), title).queue();
        }
    }
}
