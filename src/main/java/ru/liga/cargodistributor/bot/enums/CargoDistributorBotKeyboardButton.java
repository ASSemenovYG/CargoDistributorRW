package ru.liga.cargodistributor.bot.enums;

import ru.liga.cargodistributor.algorithm.enums.DistributionAlgorithmName;

public enum CargoDistributorBotKeyboardButton {
    READ_CARGO_AND_DISTRIBUTE("Прочитать посылки из файла и разложить по фургонам"),
    READ_JSON_WITH_LOADED_VANS("Прочитать json с загруженными фургонами"),
    ALGORITHM_ONE_VAN_ONE_ITEM(DistributionAlgorithmName.ONE_VAN_ONE_ITEM.getTitle()),
    ALGORITHM_SINGLE_SORTED(DistributionAlgorithmName.SINGLE_SORTED.getTitle()),
    ALGORITHM_SIMPLE_FIT(DistributionAlgorithmName.SIMPLE_FIT.getTitle()),
    ADD_CARGO_TYPE("Добавить тип посылки"),
    EDIT_CARGO_TYPE("Изменить тип посылки"),
    DELETE_CARGO_TYPE("Удалить тип посылки"),
    GET_ALL_CARGO_TYPES("Получить список всех типов посылок"),
    EDIT_CARGO_TYPE_NAME("Название посылки"),
    EDIT_CARGO_TYPE_LEGEND("Легенда посылки"),
    EDIT_CARGO_TYPE_SHAPE("Форма посылки"),
    EDIT_CARGO_TYPE_SAVE_CHANGES("Сохранить изменения посылки"),
    ADD_CARGO_VAN_TYPE("Добавить тип грузового фургона"),
    EDIT_CARGO_VAN_TYPE("Изменить тип грузового фургона"),
    DELETE_CARGO_VAN_TYPE("Удалить тип грузового фургона"),
    GET_ALL_CARGO_VAN_TYPES("Получить список всех типов грузовых фургонов"),
    EDIT_CARGO_VAN_TYPE_SAVE_CHANGES("Сохранить изменения грузового фургона"),
    EDIT_CARGO_VAN_TYPE_NAME("Название грузового фургона"),
    EDIT_CARGO_VAN_TYPE_WIDTH("Ширина грузового фургона"),
    EDIT_CARGO_VAN_TYPE_LENGTH("Длина грузового фургона");

    private final String buttonText;

    CargoDistributorBotKeyboardButton(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getButtonText() {
        return buttonText;
    }
}
