# Fight-bot-project

## Установка
Для установки необходимо заполнить параметры в файле appliction.properties.

### Токен бота
fight-bot.token=<Токен>

### Опциональные параметры
fight-bot.in-debug-mode=<Работа в режиме отладки>

fight-bot.only-active-search=<Поиск соперников только с учетом роли "Активен">

fight-bot.rank-difference=<Максимальная разница в рангах вверх>

### Настройка на каналы
fight-bot.guild-id=<ID дискорд сервера>

fight-bot.public-channel-id=<ID канала, в котором происходят вызовы>

fight-bot.history-channel-id=<ID канала, в котором будет сохраняться история вызовов>

fight-bot.rules-channel-id=<ID канала, в котором будут инициализированы правила>

fight-bot.ranks-channel-id=<ID канала, в котором будут инициализированы ранги и титулы>

fight-bot.floodilka-channel-id=<ID чат-канала>

### Настройка отдельных ролей
fight-bot.referee-role-id=<ID роли "Рефери">

fight-bot.active-status-role-id=<ID роли "Активен">

fight-bot.champion-rank-id=<ID роли "Чемпион">

### Настройка списков ролей
#### Соответствие дивизионов и ролей дивизионов
fight-bot.rankings-map.<Номер дивизиона 0-N>=<ID роли дивизиона>

#### Список ролей-титулов
fight-bot.titlesList[<Индекс>]=<ID титула>

#### Соответствие рангов и ролей рангов
fight-bot.thresholds-map.<Номер ранга 0-N>=<ID роли ранга>

### Настройка потоков
fight-bot.deleter-sleep-timeout=<Периодичность запуска потока удаления в минутах>

fight-bot.saver-sleep-timeout=<Периодичность запуска потока сохранения в минутах>

fight-bot.public-deleter-delay=<Время в минутах, через которое будет удален непринятый вызов>

fight-bot.history-deleter-delay=<Время в минутах, через которое будет удален неоконченный бой>

### Логирование
logging.level.FightBot.*=<Уровень логироваания>

logging.file.name=<Путь к файлу лога>
