package ru.liga.cargodistributor.cargo;

public class CargoItemParserException extends RuntimeException {
    CargoItemParserException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
