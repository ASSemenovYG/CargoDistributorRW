package ru.liga.cargodistributor.util;

public class WriteToFileException extends RuntimeException {
    WriteToFileException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
