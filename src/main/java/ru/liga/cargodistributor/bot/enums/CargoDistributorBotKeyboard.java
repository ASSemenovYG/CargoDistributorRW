package ru.liga.cargodistributor.bot.enums;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public enum CargoDistributorBotKeyboard {
    START,
    PICK_ALGORITHM,
    EDIT_CARGO_TYPE,
    EDIT_CARGO_VAN_TYPE;

    public static List<KeyboardRow> getKeyboardRows(CargoDistributorBotKeyboard keyboard) {
        switch (keyboard) {
            case START -> {
                return List.of(
                        new KeyboardRow(
                                CargoDistributorBotKeyboardButton.READ_CARGO_AND_DISTRIBUTE.getButtonText(),
                                CargoDistributorBotKeyboardButton.READ_JSON_WITH_LOADED_VANS.getButtonText()
                        ),
                        new KeyboardRow(
                                CargoDistributorBotKeyboardButton.ADD_CARGO_TYPE.getButtonText(),
                                CargoDistributorBotKeyboardButton.EDIT_CARGO_TYPE.getButtonText(),
                                CargoDistributorBotKeyboardButton.DELETE_CARGO_TYPE.getButtonText(),
                                CargoDistributorBotKeyboardButton.GET_ALL_CARGO_TYPES.getButtonText()
                        ),
                        new KeyboardRow(
                                CargoDistributorBotKeyboardButton.ADD_CARGO_VAN_TYPE.getButtonText(),
                                CargoDistributorBotKeyboardButton.EDIT_CARGO_VAN_TYPE.getButtonText(),
                                CargoDistributorBotKeyboardButton.DELETE_CARGO_VAN_TYPE.getButtonText(),
                                CargoDistributorBotKeyboardButton.GET_ALL_CARGO_VAN_TYPES.getButtonText()
                        )
                );
            }
            case PICK_ALGORITHM -> {
                return List.of(
                        new KeyboardRow(
                                CargoDistributorBotKeyboardButton.ALGORITHM_ONE_VAN_ONE_ITEM.getButtonText(),
                                CargoDistributorBotKeyboardButton.ALGORITHM_SINGLE_SORTED.getButtonText(),
                                CargoDistributorBotKeyboardButton.ALGORITHM_SIMPLE_FIT.getButtonText()
                        )
                );
            }
            case EDIT_CARGO_TYPE -> {
                return List.of(
                        new KeyboardRow(
                                CargoDistributorBotKeyboardButton.EDIT_CARGO_TYPE_NAME.getButtonText(),
                                CargoDistributorBotKeyboardButton.EDIT_CARGO_TYPE_LEGEND.getButtonText(),
                                CargoDistributorBotKeyboardButton.EDIT_CARGO_TYPE_SHAPE.getButtonText()
                        ),
                        new KeyboardRow(
                                CargoDistributorBotKeyboardButton.EDIT_CARGO_TYPE_SAVE_CHANGES.getButtonText()
                        )
                );
            }
            case EDIT_CARGO_VAN_TYPE -> {
                return List.of(
                        new KeyboardRow(
                                CargoDistributorBotKeyboardButton.EDIT_CARGO_VAN_TYPE_NAME.getButtonText(),
                                CargoDistributorBotKeyboardButton.EDIT_CARGO_VAN_TYPE_WIDTH.getButtonText(),
                                CargoDistributorBotKeyboardButton.EDIT_CARGO_VAN_TYPE_LENGTH.getButtonText()
                        ),
                        new KeyboardRow(
                                CargoDistributorBotKeyboardButton.EDIT_CARGO_VAN_TYPE_SAVE_CHANGES.getButtonText()
                        )
                );
            }
        }
        return null;
    }
}
