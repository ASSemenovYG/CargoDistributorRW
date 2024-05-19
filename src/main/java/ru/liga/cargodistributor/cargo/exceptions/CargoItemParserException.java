package ru.liga.cargodistributor.cargo.exceptions;

public class CargoItemParserException extends RuntimeException {
    public CargoItemParserException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
