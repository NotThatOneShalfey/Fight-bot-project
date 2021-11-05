package FightBot.commands;

import FightBot.utils.Constants;
import FightBot.core.CommandManager;
import FightBot.interfaces.ICommand;
import FightBot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HelpCommand implements ICommand {
    private CommandManager manager;
    //TODO сделать нормальный хелп для всего
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        TextChannel textChannel = event.getChannel();
        Member bot = event.getGuild().getSelfMember();

        List<ICommand> available = manager.getCommands()
                .values()
                .stream()
                .filter(ICommand ::isVisible)
                .collect(Collectors.toList());

        if (bot.hasPermission(Permission.MESSAGE_MANAGE)) {
            event.getMessage().delete().queueAfter(5L, TimeUnit.SECONDS);
        }

        if (args.isEmpty()) {
            List<String> commandNames = available.stream()
                    .map((command) -> "=" + command.getInvoke())
                    .collect(Collectors.toList());
            List<String> commandLocalNames = available.stream()
                    .map((command) -> "=" + command.getLocalInvoke())
                    .collect(Collectors.toList());

            textChannel.sendMessageEmbeds(buildEmbed(commandNames, commandLocalNames)).queue(
                    (message) -> message.delete().queueAfter(1L, TimeUnit.MINUTES)
            );
            return;
        }

        ICommand command = manager.getCommand(args.get(0).trim().toLowerCase(Locale.ROOT)) == null
                ? manager.getLocalCommand(args.get(0).trim().toLowerCase(Locale.ROOT))
                : manager.getCommand(args.get(0).trim().toLowerCase(Locale.ROOT));

        if (command != null) {
            textChannel.sendMessageEmbeds(buildHelpEmbed(command))
                    .queue(
                            (message) -> message.delete().queueAfter(1L, TimeUnit.MINUTES)
                    );
        }

        else {
            textChannel.sendMessage(Constants.ON_UNKNOWN_COMMAND_HELP_CALL).queue();
        }
    }

    @Override
    public String getHelp() {
        return Constants.ON_HELP_HELP_CALL;
    }

    @Override
    public String getInvoke() {
        return "help";
    }

    @Override
    public String getLocalInvoke() {
        return "помощь";
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    private MessageEmbed buildEmbed(List<String> commandNames, List<String> commandLocalNames) {
        EmbedBuilder eBuilder = new EmbedBuilder();
        eBuilder.setTitle("Список доступных команд")
                .setDescription("Помощь по отдельной команде можно вызвать по образцу:\n=help <название команды без '='>\n=помощь <название команды без '='>")
                .addField("", String.join("\n", commandNames), true)
                .addField("", String.join("\n", commandLocalNames), true);
        eBuilder.build();
        return eBuilder.build();
    }

    private MessageEmbed buildHelpEmbed(ICommand command) {
        EmbedBuilder eBuilder = new EmbedBuilder();
        eBuilder.setTitle("Команда =" + command.getInvoke() + "/=" + command.getLocalInvoke())
                .setDescription(command.getHelp());
        return eBuilder.build();
    }
}
