package ru.liga.cargodistributor.bot;

public enum CargoDistributorBotResponseMessage {
    DISTRIBUTE_CARGO(CargoDistributorBotKeyboardButton.READ_CARGO_AND_DISTRIBUTE.getButtonText()),
    READ_JSON_WITH_LOADED_VANS(CargoDistributorBotKeyboardButton.READ_JSON_WITH_LOADED_VANS.getButtonText()),
    SEND_FILE_WITH_CARGO("Отправь мне файл с посылками"),
    ENTER_VAN_LIMIT("Введи максимальное количество грузовых фургонов для распределения"),
    PICK_ALGORITHM("Выбери алгоритм распределения"),
    SEND_LOADED_VANS_TO_READ("Отправь мне файл с загруженными фургонами или скинь json в сообщении"),
    LOOK_WHAT_I_CAN_DO("Вот что я могу:"),

    ;
    private final String messageText;

    CargoDistributorBotResponseMessage(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageText() {
        return messageText;
    }
}
