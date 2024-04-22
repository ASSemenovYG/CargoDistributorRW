package ru.liga.cargodistributor.bot;

public class GetFileFromUpdateException extends RuntimeException {
    GetFileFromUpdateException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
