package ru.liga.cargodistributor.bot.enums;

import lombok.Getter;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;

@Getter
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
    CARGO_ITEM_TYPE_SUCCESSFULLY_ADDED("Тип посылки успешно добавлен. Название: "),
    ENTER_CARGO_TYPE_NAME_TO_DELETE("Введи название посылки для удаления"),
    CARGO_TYPE_TO_DELETE_NOT_FOUND("Не найден тип посылки для удаления с названием: "),
    CARGO_TYPE_SUCCESSFULLY_DELETED("Успешно удален тип посылки с названием: "),
    EDIT_CARGO_ENTER_CARGO_TYPE_NAME("Введи название посылки, которую хочешь изменить"),
    CARGO_TYPE_TO_EDIT_NOT_FOUND("Не найден тип посылки для изменения с названием: "),
    EDIT_CARGO_TYPE_PICK_PARAMETER("Выбери параметр типа посылки для изменения"),
    EDIT_CARGO_TYPE_ITEM_NOT_FOUND_IN_CACHE("Посылка для изменения не найдена в кеше"),
    CARGO_TYPE_SUCCESSFULLY_UPDATED("Тип посылки успешно обновлен"),
    FAILED_TO_FIND_CARGO_ITEM_TYPE_TO_UPDATE("Я не нашел в кеше данные типа посылки, который ты хотел обновить"),
    UPDATE_CARGO_TYPE_CURRENT_PARAMETERS("Текущие параметры типа посылки для обновления:"),
    UPDATE_CARGO_VAN_TYPE_CURRENT_PARAMETERS("Текущие параметры типа грузового фургона для обновления:"),
    ENTER_NEW_CARGO_TYPE_LEGEND("Введи один символ новой легенды посылки"),
    ENTER_NEW_CARGO_TYPE_NAME("Введи новое название типа посылки"),
    SEND_FILE_WITH_SINGLE_CARGO_NEW_SHAPE("Отправь мне файл с одной посылкой с новой формой"),
    NUMBER_OF_CARGO_TYPES_FOUND("Найдено типов посылок: "),
    ENTER_CARGO_VAN_TYPE_NAME("Введи название грузового фургона"),
    ENTER_CARGO_VAN_TYPE_WIDTH("Введи ширину грузового фургона"),
    ENTER_CARGO_VAN_TYPE_LENGTH("Введи длину грузового фургона"),
    CARGO_VAN_TYPE_NAME_ALREADY_EXISTS("Грузовой фургон с таким названием уже существует"),
    FAILED_TO_FIND_CARGO_VAN_TYPE_TO_INSERT("Я не нашел в кеше данные грузового фургона, который ты хотел создать"),
    FAILED_TO_FIND_CARGO_VAN_TYPE_TO_UPDATE("Я не нашел в кеше данные грузового фургона, который ты хотел обновить"),
    CARGO_VAN_TYPE_SUCCESSFULLY_ADDED("Тип грузового фургона успешно добавлен. Название: "),
    NEED_TO_ENTER_INTEGER_GREATER_THAN_ZERO("Необходимо ввести целое число больше нуля"),
    NUMBER_OF_CARGO_VAN_TYPES_FOUND("Найдено типов грузовых фургонов: "),
    ENTER_CARGO_VAN_TYPE_NAME_TO_DELETE("Введи название типа грузового фургона для удаления"),
    CARGO_VAN_TYPE_TO_DELETE_NOT_FOUND("Не найден тип грузового фургона для удаления с названием: "),
    CARGO_VAN_TYPE_SUCCESSFULLY_DELETED("Успешно удален тип грузового фургона с названием: "),
    EDIT_CARGO_VAN_ENTER_CARGO_VAN_TYPE_NAME("Введи название грузового фургона, который хочешь изменить"),
    CARGO_VAN_TYPE_TO_EDIT_NOT_FOUND("Не найден тип грузового для изменения с названием: "),
    EDIT_CARGO_VAN_TYPE_PICK_PARAMETER("Выбери параметр типа грузового фургона для изменения"),
    EDIT_CARGO_VAN_TYPE_ITEM_NOT_FOUND_IN_CACHE("Грузовой фургон для изменения не найден в кеше"),
    CARGO_VAN_TYPE_SUCCESSFULLY_UPDATED("Тип грузового фургона успешно обновлен"),
    ENTER_NEW_CARGO_VAN_TYPE_NAME("Введи новое название типа грузового фургона"),
    ENTER_NEW_CARGO_VAN_TYPE_WIDTH("Введи новую ширину грузового фургона"),
    ENTER_NEW_CARGO_VAN_TYPE_LENGTH("Введи новую длину грузового фургона"),
    DISTRIBUTE_BY_TYPES_ENTER_CARGO_VAN_TYPE_NAME("Введи название типа грузового фургона для распределения"),
    CARGO_VAN_TYPE_TO_DISTRIBUTE_NOT_FOUND("Не найден тип грузового фургона для распределения с названием: "),
    CARGO_ITEM_TYPE_TO_DISTRIBUTE_NOT_FOUND("Не найден тип посылки для распределения с названием: "),
    DISTRIBUTE_BY_TYPES_ERROR_WHILE_CREATING_CARGO_VAN("Произошла ошибка при создании экземпляра грузового фургона: "),
    DISTRIBUTE_BY_TYPES_ERROR_WHILE_CREATING_CARGO_ITEM("Произошла ошибка при создании экземпляра посылки: "),
    DISTRIBUTE_BY_TYPES_ENTER_CARGO_ITEM_TYPE_NAME("Введи название типа посылки для распределения"),
    DISTRIBUTE_BY_TYPES_ENTER_CARGO_ITEM_TYPE_COUNT("Введи количество экземпляров добавленного типа посылки для распределения"),
    DISTRIBUTE_BY_TYPES_CARGO_ITEM_TYPE_ADDED("Добавлен тип посылки:"),
    FAILED_TO_FIND_CARGO_PARAMETERS_TO_DISTRIBUTE("Я не нашел в кеше данные для распределения, заполненные на предыдущих шагах"),
    DISTRIBUTE_BY_TYPES_ADD_MORE_CARGO_TYPE_OR_CONTINUE("Добавить еще один тип посылки или перейти к выбору алгоритма?"),
    DISTRIBUTE_BY_TYPES_PICK_ALGORITHM("Выбери алгоритм для распределения типов посылок"),
    DISTRIBUTE_BY_TYPES_ENTER_VAN_LIMIT("Введи максимальное количество грузовых фургонов для распределения типов посылок"),
    DISTRIBUTE_BY_TYPES_CARGO_ITEM_TYPE_WITH_SUCH_LEGEND_ALREADY_ADDED("В списке посылок для распределения уже есть посылка с такой легендой, пожалуйста, введи другую легенду (один символ)"),
    DISTRIBUTE_BY_TYPES_AVAILABLE_LEGEND_SYMBOLS(
            "Доступны любые символы для легенды, кроме:\n" +
                    "Whitespace, Empty, Blank characters,\n" +
                    "Символ границы кузова фургона: " + CargoConverterService.VAN_BORDER_SYMBOL + ",\n" +
                    "И тех символов, которые уже используются в качестве легенды в добавленных типах посылок"),
    DISTRIBUTE_BY_TYPES_CURRENT_CARGO_ITEM_TYPE_LIST("Текущий список типов посылок, добавленных для распределения:");

    private final String messageText;

    CargoDistributorBotResponseMessage(String messageText) {
        this.messageText = messageText;
    }

}
