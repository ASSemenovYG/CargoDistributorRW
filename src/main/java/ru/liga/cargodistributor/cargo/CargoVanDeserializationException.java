package ru.liga.cargodistributor.cargo;

public class CargoVanDeserializationException extends RuntimeException {
    CargoVanDeserializationException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
