package FightBot.commands;

import FightBot.entities.Fighter;
import FightBot.interfaces.ICommand;
import FightBot.utils.Constants;
import FightBot.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.interactions.SelectionMenuImpl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ChangeCommand implements ICommand {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        PrivateChannel channel = event.getAuthor().openPrivateChannel().complete();
        Member bot = event.getGuild().getSelfMember();
        Map<Long, Fighter> fighters = Utils.getInstance().fighters;
        // Удаление сообщения команды
        if (bot.hasPermission(Permission.MESSAGE_MANAGE)) {
            event.getMessage().delete().queueAfter(5L, TimeUnit.SECONDS);
        }
        // Проверка на еще не зарегистрированного бойца
        if (fighters.get(event.getMember().getIdLong()) == null) {
            channel.sendMessage(String.format(Constants.ON_NON_EXISTING_FIGHTER_REGISTER, event.getAuthor().getAsMention())).queue(
                    (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
            );
            return;
        }

        Fighter fighter = fighters.get(event.getMember().getIdLong());

        Utils.getInstance().fighters.put(fighter.getId(), fighter);

        SelectionMenuImpl menu = new SelectionMenuImpl(Constants.SELECTION_CLASS_ID, "Класс", 1, 3, false, Constants.getOptionsList());

        channel.sendMessage("Выберите до 3х классов")
                .setActionRow(menu)
                .queue();
    }

    @Override
    public String getHelp() {
        return "Команда позволяет изменить фамилию и список классов, указанных при регистрации.\n" +
                "Команда может принимать в качестве параметра/аргумента фамилию в игре, по примеру :\n-" +
                getInvoke() + "/-" + getLocalInvoke() + " <фамилия в игре>.\n" +
                "Если параметр/аргумент не будет заполнен, то фамилия останется той же, " +
                " что была выбрана при регистрации.\n" +
                "После выполнения команды бот напишет в ПМ с предложением выбрать до 3х классов." +
                " Если нет необходимости изменять набор классов, то вы можете выбрать те же классы, что были выбраны при регистрации.";
    }

    @Override
    public String getInvoke() {
        return "change";
    }

    @Override
    public String getLocalInvoke() {
        return "редактирование";
    }

    @Override
    public boolean isVisible() {
        return false;
    }
}
