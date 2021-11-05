package FightBot.entities;

import FightBot.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FightMessage {
    private Fighter firstFighter;
    private Fighter secondFighter;
    private LocalDateTime registerDate;
    private String firstFighterWinnerDecision = "";
    private String secondFighterWinnerDecision = "";
    private boolean accepted = false;
    private boolean inProcess = false;
    private boolean hasEnded = false;
    private Long channelId;

    public MessageEmbed buildEmbed() {
        EmbedBuilder eBuilder = new EmbedBuilder();
        eBuilder.setImage("https://www.blackdesertfoundry.com/wp-content/uploads/2020/08/character_0_top_effect-817x320.jpg")
                .setColor(Utils.getRandomColor())
                .addField("Имя", this.firstFighter.getDiscordName(), true)
                .addBlankField(true)
                .addField("Имя", this.secondFighter.getDiscordName(), true)
                .addField("Ранг", this.firstFighter.getRankName(), true)
                .addBlankField(true)
                .addField("Ранг", this.secondFighter.getRankName(), true);
        if (!this.firstFighter.getTitles().isEmpty() && !this.secondFighter.getTitles().isEmpty()) {
            eBuilder.addField("Титулы", this.firstFighter.titlesAsString(), true)
                    .addBlankField(true)
                    .addField("Титулы", this.secondFighter.titlesAsString(), true);
        }
        else if (!this.firstFighter.getTitles().isEmpty()) {
            eBuilder.addField("Титулы", this.firstFighter.titlesAsString(), true)
                    .addBlankField(true)
                    .addField("Титулы", "Отсутствуют", true);
        }
        else if (!this.secondFighter.getTitles().isEmpty()) {

            eBuilder.addField("Титулы", "Отсутствуют", true)
                    .addBlankField(true)
                    .addField("Титулы", this.secondFighter.titlesAsString(), true);
        }
        eBuilder.addField("Классы", this.firstFighter.classesAsString(), true)
                .addBlankField(true)
                .addField("Классы", this.secondFighter.classesAsString(), true)
                .addField("Побед/поражений", this.firstFighter.getWins().toString() + " : " + this.firstFighter.getLoses().toString(), true)
                .addBlankField(true)
                .addField("Побед/поражений", this.secondFighter.getWins().toString() + " : " + this.secondFighter.getLoses().toString(), true)
                .addField("Побед подряд", this.firstFighter.getWinStreak().toString(), true)
                .addBlankField(true)
                .addField("Побед подряд", this.secondFighter.getWinStreak().toString(), true);

        if (isHasEnded()) {
            eBuilder.setTitle("Бой окончен");
        }
        else if (isInProcess()) {
            eBuilder.setTitle("Ожидание окончания боя");
        }
        else {
            eBuilder.setTitle("Заявка на бой");
        }
        if (!this.hasEnded && !this.firstFighterWinnerDecision.isEmpty() && !this.secondFighterWinnerDecision.isEmpty()) {
            eBuilder.addField("Выбрал победителя", this.firstFighterWinnerDecision.split("-")[1], true)
                    .addBlankField(true)
                    .addField("Выбрал победителя", this.secondFighterWinnerDecision.split("-")[1], true);
        }
        else if (!this.firstFighterWinnerDecision.isEmpty() && !this.hasEnded) {
            eBuilder.addField("Выбрал победителя", this.firstFighterWinnerDecision.split("-")[1], true)
                    .addBlankField(true)
                    .addField("Выбрал победителя", "Нет решения", true);
        }
        else if (!this.secondFighterWinnerDecision.isEmpty() && !this.hasEnded) {
            eBuilder.addField("Выбрал победителя", "Нет решения", true)
                    .addBlankField(true)
                    .addField("Выбрал победителя", this.secondFighterWinnerDecision.split("-")[1], true);
        }
        else if ((this.inProcess) && !this.hasEnded) {
            eBuilder.addField("Выбрал победителя", "Нет решения", true)
                    .addBlankField(true)
                    .addField("Выбрал победителя", "Нет решения", true);
        }
        return eBuilder.build();
    }

    public FightMessage(Fighter firstFighter, Fighter secondFighter) {
        this.firstFighter = firstFighter;
        this.secondFighter = secondFighter;
    }

}
