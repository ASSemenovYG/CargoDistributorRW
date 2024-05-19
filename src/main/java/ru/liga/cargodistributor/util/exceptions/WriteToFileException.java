package ru.liga.cargodistributor.util.exceptions;

public class WriteToFileException extends RuntimeException {
    public WriteToFileException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
