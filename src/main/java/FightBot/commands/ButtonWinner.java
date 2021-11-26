package FightBot.commands;

import FightBot.configuration.Configuration;
import FightBot.core.RankCalculation;
import FightBot.entities.FightDateLock;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
        // Если результат сошёлся или выбрал рефери - вычисляем кто выиграл по нажатой кнопке
        RankCalculation rankCalculation = new RankCalculation(guild);
        List<Fighter> fighterList;
        // Если победил первый
        if (event.getComponent().getLabel().contains(firstFighter.getDiscordName())) {
            fighterList = rankCalculation.onWinnerDecision(firstFighter, secondFighter);
        }
        // Если победил второй
        else {
            fighterList = rankCalculation.onWinnerDecision(secondFighter, firstFighter);
        }

        for (Fighter fighter : fighterList) {
            if (fighter.getId() == firstFighter.getId()) {
                firstFighter = fighter;
            }
            else {
                secondFighter = fighter;
            }
        }

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

        Utils.getInstance().fightMessages.remove(event.getMessage().getIdLong());

        Utils.getInstance().fightDatesList.add(new FightDateLock(LocalDate.now(), firstFighter.getId(), secondFighter.getId()));
    }

    @Override
    public String getInvoke() {
        return Constants.BUTTON_CLAIM_WINNER;
    }
}
