package FightBot.utils;

import lombok.Getter;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class Constants {

    @Getter
    private static final List<SelectOption> optionsList = new ArrayList<>();

    public static final String ON_REGISTER_CALL_WITHOUT_ARGS = "%s необходимо задать фамилию в игре в качестве аргумента!";
    public static final String ON_EXISTING_FIGHTER_REGISTER = "%s вы уже были зарегистрированы!";
    public static final String ON_NON_EXISTING_FIGHTER_REGISTER = "%s вы еще не были зарегистрированы!";
    public static final String ON_SOMEONE_NON_EXISTING_REGISTER = "%s еще не был зарегистрирован!";
    public static final String ON_SELF_FIGHT_CALL = "%s нельзя вызвать самого себя!";
    public static final String ON_UNKNOWN_COMMAND_HELP_CALL = "Такой команды не существует!";
    public static final String ON_HELP_HELP_CALL = "Чтобы вызвать помощь по любой из существующих команд введите =help/=помощь <название команды> без '='.";
    public static final String ON_NON_EXISTING_RANK = "%s ваш ранг не подходит для вызова соперника!";
    public static final String ON_LOW_RANK_CALL = "%s ваш ранг не подходит для вызова соперника!";
    public static final String ON_LOCKED_FIGHTER_CALL = "%s не может участвовать в бою, поскольку на него уже зарегистирован вызов!";
    public static final String ON_SELF_AS_LOCKED_CALL = "%s, вы не можете участвовать в бою, поскольку на вас уже зарегистирован вызов!";
    public static final String ON_DATE_TOO_EARLY_CALL = "%s вы не можете объявлять бой одному и тому же бойцу чаще, чем один раз в день!";
    public static final String BUTTON_ACCEPT_COMMAND_ID = "Accept";
    public static final String BUTTON_DECLINE_COMMAND_ID = "Decline";
    public static final String BUTTON_FIRST_MEMBER_WINNER_ID = "Winner-First";
    public static final String BUTTON_SECOND_MEMBER_WINNER_ID = "Winner-Second";
    public static final String BUTTON_CLAIM_WINNER = "Winner";
    public static final String SELECTION_CLASS_WARRIOR = "Class-warrior";
    public static final String SELECTION_CLASS_TAMER = "Class-tamer";
    public static final String SELECTION_CLASS_SORC = "Class-sorc";
    public static final String SELECTION_CLASS_ZERKER = "Class-zerker";
    public static final String SELECTION_CLASS_RANGER = "Class-ranger";
    public static final String SELECTION_CLASS_BLADER = "Class-blader";
    public static final String SELECTION_CLASS_MAEHWA = "Class-maehwa";
    public static final String SELECTION_CLASS_NINJA = "Class-ninja";
    public static final String SELECTION_CLASS_KUNOICHI = "Class-kunoichi";
    public static final String SELECTION_CLASS_WIZARD = "Class-wizard";
    public static final String SELECTION_CLASS_ENCHANTRESS = "Class-enchantress";
    public static final String SELECTION_CLASS_STRIKER = "Class-striker";
    public static final String SELECTION_CLASS_MYSTIC = "Class-mystic";
    public static final String SELECTION_CLASS_DK = "Class-dk";
    public static final String SELECTION_CLASS_VALKYRIE = "Class-valkyrie";
    public static final String SELECTION_CLASS_HASHASHIN = "Class-hashashin";
    public static final String SELECTION_CLASS_CORSAIR = "Class-corsair";
    public static final String SELECTION_CLASS_GUARDIAN = "Class-guardian";
    public static final String SELECTION_CLASS_ARCHER = "Class-archer";
    public static final String SELECTION_CLASS_SHAI = "Class-shai";
    public static final String SELECTION_CLASS_NOVA = "Class-nova";
    public static final String SELECTION_CLASS_LAHN = "Class-lahn";
    public static final String SELECTION_CLASS_SAGE = "Class-sage";
    public static final String SELECTION_CLASS_ID = "Class";

    @PostConstruct
    private void init() {
        optionsList.add(SelectOption.of("Воин", SELECTION_CLASS_WARRIOR));
        optionsList.add(SelectOption.of("Мистик", SELECTION_CLASS_TAMER));
        optionsList.add(SelectOption.of("Гигант", SELECTION_CLASS_ZERKER));
        optionsList.add(SelectOption.of("Колдунья", SELECTION_CLASS_SORC));
        optionsList.add(SelectOption.of("Лучница", SELECTION_CLASS_RANGER));
        optionsList.add(SelectOption.of("Мастер меча", SELECTION_CLASS_BLADER));
        optionsList.add(SelectOption.of("Маева", SELECTION_CLASS_MAEHWA));
        optionsList.add(SelectOption.of("Ниндзя", SELECTION_CLASS_NINJA));
        optionsList.add(SelectOption.of("Куноичи", SELECTION_CLASS_KUNOICHI));
        optionsList.add(SelectOption.of("Волшебник", SELECTION_CLASS_WIZARD));
        optionsList.add(SelectOption.of("Волшебница", SELECTION_CLASS_ENCHANTRESS));
        optionsList.add(SelectOption.of("Страйкер", SELECTION_CLASS_STRIKER));
        optionsList.add(SelectOption.of("Фурия", SELECTION_CLASS_MYSTIC));
        optionsList.add(SelectOption.of("Темный рыцарь", SELECTION_CLASS_DK));
        optionsList.add(SelectOption.of("Валькирия", SELECTION_CLASS_VALKYRIE));
        optionsList.add(SelectOption.of("Хассашин", SELECTION_CLASS_HASHASHIN));
        optionsList.add(SelectOption.of("Страж", SELECTION_CLASS_GUARDIAN));
        optionsList.add(SelectOption.of("Лучник", SELECTION_CLASS_ARCHER));
        optionsList.add(SelectOption.of("Шай", SELECTION_CLASS_SHAI));
        optionsList.add(SelectOption.of("Нова", SELECTION_CLASS_NOVA));
        optionsList.add(SelectOption.of("Лан", SELECTION_CLASS_LAHN));
        optionsList.add(SelectOption.of("Мудрец", SELECTION_CLASS_SAGE));
        optionsList.add(SelectOption.of("Корсар", SELECTION_CLASS_CORSAIR));
    }
}
