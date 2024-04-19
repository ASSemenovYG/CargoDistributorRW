package ru.liga.cargodistributor.cargo;

public class CargoItemValidationException extends IllegalArgumentException{
    public CargoItemValidationException(String errorMessage) {
        super(errorMessage);
    }
}
