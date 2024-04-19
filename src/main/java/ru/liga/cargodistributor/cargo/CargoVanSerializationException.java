package ru.liga.cargodistributor.cargo;

public class CargoVanSerializationException extends RuntimeException {
    CargoVanSerializationException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
