package FightBot.commands;

import FightBot.configuration.Configuration;
import FightBot.entities.Fighter;
import FightBot.utils.Constants;
import FightBot.utils.Utils;
import FightBot.entities.FightMessage;
import FightBot.interfaces.ICommand;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.Button;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static FightBot.utils.Constants.ON_NON_AVAILABLE_CALL;

@Slf4j
public class FightCommand implements ICommand {
    AvailableCommand command = new AvailableCommand();
    @SneakyThrows
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != Configuration.getInstance().getPublicChannelId()) {
            return;
        }

        TextChannel textChannel = event.getChannel();
        Member bot = event.getGuild().getSelfMember();
        Map<Long, Long> rankingsMap = Configuration.getInstance().getRankingsMap();

        List<User> mentionedUsers = event.getMessage().getMentionedUsers();

        // Удаление сообщения команды
        if (bot.hasPermission(Permission.MESSAGE_MANAGE)) {
            event.getMessage().delete().queueAfter(5L, TimeUnit.SECONDS);
        }
        // Проверка на вызов самого себя
        if (mentionedUsers.size() != 1 || mentionedUsers.contains(event.getAuthor())) {
            textChannel.sendMessage(String.format(Constants.ON_SELF_FIGHT_CALL, event.getAuthor().getAsMention())).queue(
                    (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
            );
            return;
        }

        List<Long> lockedFightersList = Utils.getInstance().lockedFightersList;
        Map<Long, Fighter> fighters = Utils.getInstance().fighters;

        Fighter firstFighter = fighters.get(event.getMember().getIdLong());
        Fighter secondFighter = fighters.get(mentionedUsers.get(0).getIdLong());
        // Проверка зареган ли первый боец
        if (firstFighter == null || firstFighter.getClasses().isEmpty()) {
            textChannel.sendMessage(String.format(Constants.ON_NON_EXISTING_FIGHTER_REGISTER, event.getAuthor().getAsMention())).queue(
                    (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
            );
            return;
        }

        // Проверка зареган ли второй боец
        if (secondFighter == null || secondFighter.getClasses().isEmpty()) {
            textChannel.sendMessage(String.format(Constants.ON_SOMEONE_NON_EXISTING_REGISTER, event.getGuild().getMemberById(mentionedUsers.get(0).getIdLong()).getAsMention())).queue(
                    (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
            );
            return;
        }

        // Проверка залочен ли второй боец
        if (lockedFightersList.contains(secondFighter.getId())) {
            textChannel.sendMessage(String.format(Constants.ON_LOCKED_FIGHTER_CALL, secondFighter.getDiscordName())).queue(
                    (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
            );
            return;
        }

        // Проверка залочен ли первый боец
        if (lockedFightersList.contains(firstFighter.getId())) {
            textChannel.sendMessage(String.format(Constants.ON_SELF_AS_LOCKED_CALL, firstFighter.getDiscordName())).queue(
                    (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
            );
            return;
        }

        // Здесь проверка, если не нашли ранги
        if (firstFighter.getRank() == null || secondFighter.getRank() == null) {
            textChannel.sendMessage(String.format(Constants.ON_NON_EXISTING_RANK, event.getAuthor().getAsMention())).queue(
                    (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
            );
            return;
        }

        // Проверка на дату и ранги
        if (!Utils.getInstance().getListOfAvailableFighters(firstFighter).contains(secondFighter)) {
            textChannel.sendMessage(String.format(ON_NON_AVAILABLE_CALL, event.getAuthor().getAsMention())).queue(
                    (message) -> message.delete().queueAfter(15L, TimeUnit.SECONDS)
            );
            return;
        }
//        // Проверка на дату
//        if (!Configuration.getInstance().isInDebugMode()
//                && !Utils.getInstance().fightDatesList.isEmpty()
//                && Utils.getInstance().fightDatesList.contains(new FightDateLock(LocalDate.now(), firstFighter.getId(), secondFighter.getId()))) {
//            textChannel.sendMessage(String.format(Constants.ON_DATE_TOO_EARLY_CALL, event.getAuthor().getAsMention())).queue(
//                    (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
//            );
//            return;
//        }
//
//        // Здесь проверка если разница в рангах не проходит
//        if (secondFighter.getRank() - firstFighter.getRank() > Configuration.getInstance().getRankDifference()
//                || firstFighter.getRank() > secondFighter.getRank()) {
//            textChannel.sendMessage(String.format(Constants.ON_LOW_RANK_CALL, event.getAuthor().getAsMention())).queue(
//                    (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
//            );
//            return;
//        }

        FightMessage fightMessage = new FightMessage(firstFighter, secondFighter);
        fightMessage.setChannelId(event.getChannel().getIdLong());

        // Синхронизация рангов первого бойца
        List<Role> rolesToRemove = new ArrayList<>();

        Long fighterGuildRoleId = rankingsMap.get(firstFighter.getRank());
        List<Role> memberRoles = event.getMember().getRoles();
        List<Role> fighterRoleAsList = new ArrayList<>();
        fighterRoleAsList.add(event.getGuild().getRoleById(fighterGuildRoleId));

        for (Role role : memberRoles) {
            if (rankingsMap.containsValue(role.getIdLong()) && role.getIdLong() != fighterGuildRoleId) {
                rolesToRemove.add(role);
            }
        }
        event.getGuild().modifyMemberRoles(event.getMember(), fighterRoleAsList, rolesToRemove).queue();

        // Синхронизация рангов второго бойца
        rolesToRemove.clear();
        fighterRoleAsList.clear();

        fighterGuildRoleId = rankingsMap.get(secondFighter.getRank());
        memberRoles = event.getGuild().getMember(mentionedUsers.get(0)).getRoles();
        fighterRoleAsList.add(event.getGuild().getRoleById(fighterGuildRoleId));

        for (Role role : memberRoles) {
            if (rankingsMap.containsValue(role.getIdLong()) && role.getIdLong() != fighterGuildRoleId) {
                rolesToRemove.add(role);
            }
        }
        event.getGuild().modifyMemberRoles(event.getGuild().getMember(mentionedUsers.get(0)), fighterRoleAsList, rolesToRemove).queue();

        // Создаем форму
        Message message = textChannel.sendMessage(event.getGuild().getMemberById(firstFighter.getId()).getAsMention() + event.getGuild().getMemberById(secondFighter.getId()).getAsMention())
                .setEmbeds(fightMessage.buildEmbed())
                .setActionRow(Button.success(Constants.BUTTON_ACCEPT_COMMAND_ID, "Принять"), Button.danger(Constants.BUTTON_DECLINE_COMMAND_ID, "Отклонить"))
                .complete();


        // Локируем бойцов
        lockedFightersList.add(firstFighter.getId());
        lockedFightersList.add(secondFighter.getId());

        // Меняем статус файта
        fightMessage.setAccepted(true);
        fightMessage.setRegisterDate(LocalDateTime.now());

        Utils.getInstance().fightMessages.put(message.getIdLong(), fightMessage);
    }

    @Override
    public String getHelp() {
        return "Команда позволяет вызвать кого-то из зарегистрированных бойцов на бой.\n" +
                "Для вызова необходимо, чтобы вы и вызываемый боец были зарегистрированы," +
                " а также не имели активный бой.\n" +
                "Команда вызывается по примеру :\n=" +
                getInvoke() + "/=" + getLocalInvoke() + " @<имя в дискорде>." +
                " Доступных вам для вызова бойцов можно проверить выполнив команду :\n=" +
                command.getInvoke() + "/=" + command.getLocalInvoke();
    }

    @Override
    public String getInvoke() {
        return "fight";
    }

    @Override
    public String getLocalInvoke() {
        return "бой";
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
