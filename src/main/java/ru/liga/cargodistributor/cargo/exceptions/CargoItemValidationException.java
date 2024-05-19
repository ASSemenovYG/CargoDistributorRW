package ru.liga.cargodistributor.cargo.exceptions;

public class CargoItemValidationException extends IllegalArgumentException{
    public CargoItemValidationException(String errorMessage) {
        super(errorMessage);
    }
}
