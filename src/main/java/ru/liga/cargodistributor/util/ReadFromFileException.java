package ru.liga.cargodistributor.util;

public class ReadFromFileException extends RuntimeException {
    ReadFromFileException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
