package ru.liga.cargodistributor.util.exceptions;

public class FromMultipartFileToFileException extends RuntimeException {
    public FromMultipartFileToFileException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
