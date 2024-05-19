package ru.liga.cargodistributor.util.exceptions;

public class ReadFromFileException extends RuntimeException {
    public ReadFromFileException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
