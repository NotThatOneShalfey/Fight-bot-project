package FightBot.commands;

import FightBot.configuration.Configuration;
import FightBot.entities.Fighter;
import FightBot.interfaces.ICommand;
import FightBot.utils.Constants;
import FightBot.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.interactions.SelectionMenuImpl;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RegisterCommand implements ICommand {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        PrivateChannel channel = event.getAuthor().openPrivateChannel().complete();
        Member bot = event.getGuild().getSelfMember();
        Guild guild = Utils.getInstance().getManager().getGuildById(Configuration.getInstance().getGuildId());
        // Удаление сообщения команды
        if (bot.hasPermission(Permission.MESSAGE_MANAGE)) {
            event.getMessage().delete().queueAfter(5L, TimeUnit.SECONDS);
        }
        // Если фамилия не была подана на вход
//        if (args.isEmpty()) {
//            channel.sendMessage(String.format(Constants.ON_REGISTER_CALL_WITHOUT_ARGS, event.getAuthor().getAsMention())).queue(
//                    (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
//            );
//            return;
//        }
        // Если это уже зарегистрированный пользователь
//        if (Utils.getInstance().fighters.get(event.getMember().getIdLong()) != null) {
//            channel.sendMessage(String.format(Constants.ON_EXISTING_FIGHTER_REGISTER, event.getAuthor().getAsMention())).queue(
//                    (message) -> message.delete().queueAfter(5L, TimeUnit.SECONDS)
//            );
//            return;
//        }

        Fighter fighter = new Fighter(event.getMember());
        fighter.setRank(0L);

        Role role = event.getGuild().getRoleById(Configuration.getInstance().rankingsMap.get(0L));
        fighter.setRankName(role.getName());


        guild.addRoleToMember(event.getMember(), guild.getRoleById(Configuration.getInstance().rankingsMap.get(0L))).queue();

        guild.modifyMemberRoles(event.getMember(), guild.getRoleById(Configuration.getInstance().rankingsMap.get(0L))).queue();

        Utils.getInstance().fighters.put(event.getMember().getIdLong(), fighter);

        SelectionMenuImpl menu = new SelectionMenuImpl(Constants.SELECTION_CLASS_ID, "Класс", 1, 3, false, Constants.getOptionsList());
        channel.sendMessage("Выберите до 3х классов")
                .setActionRow(menu)
                .queue();
    }

    @Override
    public String getHelp() {
        return "Команда позволяет зарегистрировать себя для участия в боях.\n" +
                "После выполнения команды бот напишет в ПМ с предложением выбрать до 3х классов.\n" +
                "После выбора классов, вы будете зарегистрированы.";
    }

    @Override
    public String getInvoke() {
        return "register";
    }

    @Override
    public String getLocalInvoke() {
        return "регистрация";
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
