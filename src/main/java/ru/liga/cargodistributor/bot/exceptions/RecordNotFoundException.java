package ru.liga.cargodistributor.bot.exceptions;

public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
