package ru.liga.cargodistributor.bot.enums;

import lombok.Getter;

@Getter
public enum CargoDistributorBotUserCommand {
    START("/start"),
    DISTRIBUTE("/distribute"),
    READ_CARGO("/readcargo"),
    HELP("/help"),
    ABOUT("/about");

    private final String commandText;

    CargoDistributorBotUserCommand(String commandText) {
        this.commandText = commandText;
    }

}
