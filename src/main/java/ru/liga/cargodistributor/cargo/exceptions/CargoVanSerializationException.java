package ru.liga.cargodistributor.cargo.exceptions;

public class CargoVanSerializationException extends RuntimeException {
    public CargoVanSerializationException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
