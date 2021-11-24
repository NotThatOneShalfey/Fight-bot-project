package FightBot.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fighter {
    private long id;
    private String discordName;
    private Long rank;
    private String rankName;
    private List<String> classes = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private Long threshold = 0L;
    private Long wins = 0L;
    private Long loses = 0L;
    private Long winStreak = 0L;
    private boolean active = false;

    public Fighter(Member member) {
        this.id = member.getIdLong();
        this.discordName = member.getEffectiveName();
    }

    public MessageEmbed buildEmbed() {
        EmbedBuilder eBuilder = new EmbedBuilder();
        String classesString = "";
        for (String str : classes) {
            classesString = classesString + str + "\n";
        }
        eBuilder.setTitle(this.discordName)
                .addField("Ранг", this.rankName,false);
        if (!titles.isEmpty()) {
            eBuilder.addField("Титулы", titlesAsString(), false);
        }
        else {
            eBuilder.addField("Титулы", "Отсутствуют", false);
        }
        eBuilder.addField("Классы", classesString, false)
                .addField("Побед/поражений", this.wins.toString() + " : " + this.loses.toString(), false)
                .addField("Побед подряд", this.winStreak.toString(), false)
                .addField("Статус", this.isActive() ? "Активен" : "Неактивен", false);
        return eBuilder.build();
    }

    public String classesAsString() {
        return String.join("\n", this.classes);
    }
    public String titlesAsString() {return String.join("\n", this.titles);}
}
