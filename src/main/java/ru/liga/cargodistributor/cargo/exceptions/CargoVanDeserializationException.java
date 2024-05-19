package ru.liga.cargodistributor.cargo.exceptions;

public class CargoVanDeserializationException extends RuntimeException {
    public CargoVanDeserializationException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
