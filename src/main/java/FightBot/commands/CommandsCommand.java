package FightBot.commands;

import FightBot.interfaces.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandsCommand implements ICommand {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        PrivateChannel channel = event.getAuthor().openPrivateChannel().complete();
        Member bot = event.getGuild().getSelfMember();

        if (bot.hasPermission(Permission.MESSAGE_MANAGE)) {
            event.getMessage().delete().queueAfter(5L, TimeUnit.SECONDS);
        }

        List<ActionRow> actionRows = new ArrayList<>();

        List<Button> buttonList1 = new ArrayList<>() {
            {
                add(Button.primary("Случайно", "Случайный соперник"));
                add(Button.primary("Таблица", "Доступные соперники"));
                add(Button.primary("Топ", "Топ игроков"));
            }
        };

        List<Button> buttonList2 = new ArrayList<>() {
            {
                add(Button.primary("Ранг", "Мой ранг"));
                add(Button.success("Активный", "Активный"));
                add(Button.danger("Неактивный", "Неактивный"));
            }
        };

        actionRows.add(ActionRow.of(buttonList1));
        actionRows.add(ActionRow.of(buttonList2));

        // Создаем форму
        channel.sendMessage("Выберите команду:")
                .setActionRows(actionRows)
                .queue(message -> message.delete().queueAfter(120L, TimeUnit.SECONDS));
    }

    @Override
    public String getHelp() {
        return "Бот отправляет в личные сообщения форму с кнопками. " +
                "Если вам удобнее работать через кнопки, чем через отдельные команды, рекомендуем использовать такой способ. " +
                "Также бот принимает эту команду не только в каналы сервера, но и в личные сообщения.";
    }

    @Override
    public String getInvoke() {
        return "commands";
    }

    @Override
    public String getLocalInvoke() {
        return "команды";
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
