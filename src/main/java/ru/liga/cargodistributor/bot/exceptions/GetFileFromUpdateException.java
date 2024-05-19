package ru.liga.cargodistributor.bot.exceptions;

public class GetFileFromUpdateException extends RuntimeException {
    public GetFileFromUpdateException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
