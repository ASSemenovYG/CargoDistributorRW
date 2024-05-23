package ru.liga.cargodistributor.bot.enums;

public enum CargoDistributorBotResponseMessage {
    DISTRIBUTE_CARGO(CargoDistributorBotKeyboardButton.READ_CARGO_AND_DISTRIBUTE.getButtonText()),
    READ_JSON_WITH_LOADED_VANS(CargoDistributorBotKeyboardButton.READ_JSON_WITH_LOADED_VANS.getButtonText()),
    SEND_FILE_WITH_CARGO("Отправь мне файл с посылками"),
    SEND_FILE_WITH_SINGLE_CARGO("Отправь мне файл с одной посылкой"),
    ENTER_VAN_LIMIT("Введи максимальное количество грузовых фургонов для распределения"),
    PICK_ALGORITHM("Выбери алгоритм распределения"),
    SEND_LOADED_VANS_TO_READ("Отправь мне файл с загруженными фургонами или скинь json в сообщении"),
    LOOK_WHAT_I_CAN_DO("Вот что я могу:"),
    ERROR_WHILE_PROCESSING_CARGO_FILE("Произошла ошибка при обработке файла с посылками:"),
    ERROR_WHILE_PROCESSING_CARGO_VAN_FILE("Произошла ошибка при обработке файла с загруженными фургонами:"),
    ERROR_WHILE_PROCESSING_CARGO_VAN_JSON_MESSAGE("Произошла ошибка при обработке JSON с загруженными фургонами:"),
    TRY_AGAIN("Попробуй еще раз"),
    NO_CARGO_ITEMS_FOUND_IN_A_FILE("В файле не найдено ни одной посылки!"),
    MORE_THAN_ONE_CARGO_ITEM_FOUND_IN_A_FILE("В файле не найдено ни одной посылки!"),
    FOUND_CARGO_ITEMS_IN_A_FILE("В файле найдены следующие посылки:"),
    ENTER_CARGO_VAN_LIMIT("Введи максимальное количество грузовых фургонов для распределения"),
    FAILED_TO_PARSE_INTEGER("Не могу распознать число. Необходимо ввести целое число"),
    FAILED_TO_FIND_CARGO_LIST("Я не нашел твой список посылок, который ты отправлял до этого"),
    FAILED_TO_DISTRIBUTE_UNDER_VAN_LIMIT("Не удалось распределить посылки из файла по указанному количеству фургонов"),
    FAILED_TO_FIND_CARGO_ITEM_TYPE_DATA("Я не нашел данные для типа посылки, которые ты оправлял ранее"),
    DISTRIBUTION_RESULT("Результат распределения посылок по грузовым фургонам:"),
    ERROR_WHILE_CREATING_DISTRIBUTION_RESULT_FILE("Произошла ошибка при формировании файла с результатами распределения"),
    DISTRIBUTION_RESULT_IN_A_FILE("Результат распределения в файле:"),
    NUMBER_OF_READ_VANS("Количество обнаруженных фургонов: "),
    DISTRIBUTION_OF_CARGO_FROM_VANS("Распределение посылок:"),
    CARGO_LIST_FROM_VANS("Общий список посылок из файла:"),
    NUMBER_OF_ITEMS_FROM_VANS("Общее количество посылок из файла: "),
    RETURNING_TO_START("Возвращаюсь в начало"),
    CANT_RESOLVE_PICKED_ALGORITHM_NAME("Я не понял, какой алгоритм ты выбрал"),
    CANT_PROCESS_LAST_MESSAGE("Я не понял, как обработать последнее сообщение"),
    CANT_PROCESS_LAST_MESSAGE_FOUND_PREVIOUS_RESPONSE("Я не понял, как обработать последнее сообщение, вот последнее, о чем я тебя просил:"),
    HELP_DISTRIBUTE_COMMAND_DESCRIPTION(
            """
                    Бот умеет распределять посылки по заданному количеству фургонов используя алгоритм по выбору.
                    Посылки берутся из файла, максимальный размер посылки - 9 клеток, посылки могут быть только прямоугольными
                    Грузовой фургон двумерный, размером 6x6 клеток
                    Пример файла:
                    """
    ),
    HELP_DISTRIBUTE_COMMAND_RUN(
            """
                    Для запуска функции распределения используй команду /distribute
                    Или используй команду /start и нажми на соответствующую кнопку
                    """
    ),
    HELP_READCARGO_COMMAND_DESCRIPTION(
            """
                    Еще бот умеет считывать загруженные фургоны из JSON.
                    Можешь скинуть JSON файлом или прямо в сообщении
                    Пример файла:
                    """
    ),
    HELP_READCARGO_COMMAND_RUN(
            """
                    Для запуска функции считывания используй команду /readcargo
                    Или используй команду /start и нажми на соответствующую кнопку
                    """
    ),
    ERROR_WHILE_READING_FROM_FILE_MESSAGE("Произошла ошибка при чтении данных из файла"),
    ERROR_WHILE_READING_FROM_FILE_FOUND_PREVIOUS_RESPONSE("Вот последнее, о чем я тебя просил:"),
    ENTER_CARGO_TYPE_NAME("Введи название посылки"),
    ENTER_CARGO_TYPE_LEGEND("Введи один символ легенды посылки"),
    INCORRECT_CARGO_TYPE_LEGEND("Введены некорректные данные для легенды посылки"),
    CARGO_TYPE_NAME_ALREADY_EXISTS("Посылка с таким названием уже существует"),
    CARGO_ITEM_TYPE_SUCCESSFULLY_ADDED("Тип посылки успешно добавлен. Название: ");

    private final String messageText;

    CargoDistributorBotResponseMessage(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageText() {
        return messageText;
    }
}
