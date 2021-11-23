package FightBot.core;

import FightBot.commands.OnRolesChangeHandler;
import FightBot.configuration.Configuration;
import FightBot.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class Listener extends ListenerAdapter {

    private final CommandManager manager = new CommandManager();
    private final DeleterThread deleterThread = new DeleterThread();
    private final SaverThread saverThread = new SaverThread();
    private final OnRolesChangeHandler rolesHandler = new OnRolesChangeHandler();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Utils.getInstance().checkRolesOnCall();
        Utils.getInstance().checkMembersOnCall();
        log.info("Hello!! Now you are up to work!");
        saverThread.setDaemon(true);
        saverThread.start();
        deleterThread.setDaemon(true);
        deleterThread.start();
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.isWebhookMessage()) {
            return;
        }

        if (event.getMessage().getContentRaw().startsWith(Configuration.getInstance().getPrefix())) {
            manager.handleCommand(event);
        }

        if (event.getMessage().getContentRaw().equals("=shutdown") && event.getAuthor().getIdLong() == Configuration.getInstance().getOwnerId()) {
            event.getMessage().delete().complete();
            Utils.getInstance().getManager().shutdown();
            System.exit(0);
        }

        if (event.getMessage().getContentRaw().equals("=check") && event.getAuthor().getIdLong() == Configuration.getInstance().getOwnerId()) {
            event.getMessage().delete().complete();
            Utils.getInstance().checkRolesOnCall();
        }

    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        manager.handleButtonClick(event);
        event.deferEdit().queue();
    }

    @Override
    public void onSelectionMenu(@NotNull SelectionMenuEvent event) {
        event.deferEdit().queue();
        manager.handleSelectionMenuCommand(event);
    }

    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        rolesHandler.handleRolesAdd(event);
    }

    @Override
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        rolesHandler.handleRolesRemove(event);
    }
}
