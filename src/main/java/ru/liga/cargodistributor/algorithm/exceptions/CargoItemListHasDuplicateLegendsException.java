package ru.liga.cargodistributor.algorithm.exceptions;

public class CargoItemListHasDuplicateLegendsException extends RuntimeException {
    public CargoItemListHasDuplicateLegendsException(String errorMessage) {
        super(errorMessage);
    }
}
