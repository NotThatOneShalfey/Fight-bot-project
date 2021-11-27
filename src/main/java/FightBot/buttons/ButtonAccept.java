package FightBot.buttons;

import FightBot.configuration.Configuration;
import FightBot.utils.Constants;
import FightBot.utils.Utils;
import FightBot.entities.FightMessage;
import FightBot.interfaces.IButtonCommand;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
public class ButtonAccept implements IButtonCommand {
    @SneakyThrows
    @Override
    public void handle(String arg, ButtonClickEvent event) {
        FightMessage fightMessage = Utils.getInstance().fightMessages.get(event.getMessage().getIdLong());
        TextChannel textChannel = Utils.getInstance().getManager().getTextChannelById(Configuration.getInstance().getHistoryChannelId());

        if (Objects.isNull(fightMessage)) {
            return;
        }

        if (!Configuration.getInstance().isInDebugMode()) {
            if (event.getMember().getIdLong() != fightMessage.getSecondFighter().getId()) {
                return;
            }
        }

        event.getMessage().delete().queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
        Utils.getInstance().fightMessages.remove(event.getMessage().getIdLong());

        fightMessage.setChannelId(textChannel.getIdLong());
        fightMessage.setAccepted(false);
        fightMessage.setInProcess(true);

        textChannel.sendMessage(event.getGuild().getMemberById(fightMessage.getFirstFighter().getId()).getAsMention()
                        + event.getGuild().getMemberById(fightMessage.getSecondFighter().getId()).getAsMention())
                .setEmbeds(fightMessage.buildEmbed())
                .setActionRow(Button.primary(Constants.BUTTON_FIRST_MEMBER_WINNER_ID, "Победитель - " + fightMessage.getFirstFighter().getDiscordName())
                        , Button.primary(Constants.BUTTON_SECOND_MEMBER_WINNER_ID, "Победитель - " + fightMessage.getSecondFighter().getDiscordName()))
                .queue(message -> {
                    fightMessage.setRegisterDate(LocalDateTime.now());
                    Utils.getInstance().fightMessages.put(message.getIdLong(), fightMessage);
                });
    }

    @Override
    public String getInvoke() {
        return Constants.BUTTON_ACCEPT_COMMAND_ID;
    }
}
