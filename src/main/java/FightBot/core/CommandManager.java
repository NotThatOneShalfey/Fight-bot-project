package FightBot.core;

import FightBot.commands.*;
import FightBot.configuration.Configuration;
import FightBot.interfaces.IButtonCommand;
import FightBot.interfaces.ICommand;
import FightBot.interfaces.ISelectionMenuCommand;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.*;
import java.util.regex.Pattern;

@Data
@Slf4j
public class CommandManager {
    private Map<String, ICommand> commands = new HashMap<>();
    private Map<String, IButtonCommand> buttonCommands = new HashMap<>();
    private Map<String, ISelectionMenuCommand> selectionCommands = new HashMap<>();
    private Map<String, ICommand> localInvokeCommands = new HashMap<>();


	// Some comment
    public void addSelectionMenu(ISelectionMenuCommand menu) {
        if (!this.selectionCommands.containsKey(menu.getInvoke())) {
            this.selectionCommands.put(menu.getInvoke(), menu);
        }
    }
    // Another comment
    public ICommand getCommand(String name) {
        return this.commands.get(name);
    }

    public ICommand getLocalCommand(String name) {
        return this.localInvokeCommands.get(name);
    }

    private void addCommand(ICommand command) {
        if (!this.commands.containsKey(command.getInvoke())) {
            this.commands.put(command.getInvoke(), command);
        }
        if (!this.localInvokeCommands.containsKey(command.getLocalInvoke())) {
            this.localInvokeCommands.put(command.getLocalInvoke(), command);
        }
    }

    private void addButtonCommand(IButtonCommand buttonCommand) {
        if (!this.buttonCommands.containsKey(buttonCommand.getInvoke())) {
            this.buttonCommands.put(buttonCommand.getInvoke(), buttonCommand);
        }
    }

    public void handleSelectionMenuCommand(SelectionMenuEvent event) {
        String menu = event.getComponentId().split("-")[0];
        log.info("SelectionMenu command - {}, Caller id = {}, name = {}", menu, event.getUser().getIdLong(), event.getUser().getName());
        if (selectionCommands.containsKey(menu)) {
            selectionCommands.get(menu).handle(event);
        }
    }

    public void handleCommand(GuildMessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != Configuration.getInstance().getPublicChannelId()) {
            return;
        }
        final String[] split = event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(Configuration.getInstance().getPrefix()), "").split("\\s+");
        final String commandName = split[0].toLowerCase();
        log.info("Regular command - {}, Caller id = {}, name = {}", commandName, event.getMember().getIdLong(), event.getMember().getEffectiveName());
        if (commands.containsKey(commandName)) {
            final List<String> args = new ArrayList<>(Arrays.asList(split).subList(1, split.length));
            commands.get(commandName).handle(args, event);
        }
        if (localInvokeCommands.containsKey(commandName)) {
            final List<String> args = new ArrayList<>(Arrays.asList(split).subList(1, split.length));
            localInvokeCommands.get(commandName).handle(args, event);
        }
    }

    public void handleButtonClick(ButtonClickEvent event) {
        log.info("Button command - {}, Caller id = {}, name = {}", event.getComponentId(), event.getMember().getIdLong(), event.getMember().getEffectiveName());
        String command = event.getComponentId().split("-")[0];
        String arg = "";
        if (event.getComponentId().split("-").length > 1) {
            arg = event.getComponentId().split("-")[1];
        }
        if (buttonCommands.containsKey(command)) {
            buttonCommands.get(command).handle(arg, event);
        }
    }

    public CommandManager() {
        this.addCommand(new FightCommand());
        this.addCommand(new HelpCommand(this));
        this.addCommand(new RegisterCommand());
        this.addCommand(new RankCommand());
        this.addCommand(new AvailableCommand());
        this.addCommand(new CancelCommand());
        this.addCommand(new ActiveCommand());
        this.addCommand(new InactiveCommand());
        this.addCommand(new RandomCommand());
        this.addButtonCommand(new ButtonAccept());
        this.addButtonCommand(new ButtonDecline());
        this.addButtonCommand(new ButtonWinner());
        this.addSelectionMenu(new ClassSelection());
    }
}
