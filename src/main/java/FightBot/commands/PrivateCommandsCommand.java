package FightBot.commands;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PrivateCommandsCommand {
    public void handle(PrivateMessageReceivedEvent event) {
        PrivateChannel channel = event.getChannel();

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
        actionRows.add(ActionRow.of(Button.secondary("Удалить", "Удалить")));

        // Создаем форму
        channel.sendMessage("Выберите команду:")
                .setActionRows(actionRows)
                .queue(message -> message.delete().queueAfter(300L, TimeUnit.SECONDS));
    }
}
