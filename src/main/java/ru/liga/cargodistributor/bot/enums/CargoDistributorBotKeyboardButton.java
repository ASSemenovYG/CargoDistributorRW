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
    EDIT_CARGO_TYPE_NAME("Название"),
    EDIT_CARGO_TYPE_LEGEND("Легенда"),
    EDIT_CARGO_TYPE_SHAPE("Форма"),
    EDIT_CARGO_TYPE_SAVE_CHANGES("Сохранить изменения");

    private final String buttonText;

    CargoDistributorBotKeyboardButton(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getButtonText() {
        return buttonText;
    }
}
