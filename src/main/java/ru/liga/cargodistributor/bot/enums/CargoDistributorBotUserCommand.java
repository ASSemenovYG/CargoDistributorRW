package ru.liga.cargodistributor.bot.enums;

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

    public String getCommandText() {
        return commandText;
    }
}
