package ru.liga.cargodistributor.cargo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.liga.cargodistributor.cargo.exceptions.CargoItemValidationException;


class CargoItemTest {

    @Test
    void createCargoItemsWithIncorrectParameters() {
        Assertions.assertThrows(CargoItemValidationException.class, () ->
                new CargoItem(0,1,1));

        Assertions.assertThrows(CargoItemValidationException.class, () ->
                new CargoItem(1,0,1));

        Assertions.assertThrows(CargoItemValidationException.class, () ->
                new CargoItem(1,1,0));

        Assertions.assertThrows(CargoItemValidationException.class, () ->
                new CargoItem(-100,0,100));

        Assertions.assertThrows(CargoItemValidationException.class, () ->
                new CargoItem(10,5,5));

        Assertions.assertThrows(CargoItemValidationException.class, () ->
                new CargoItem(9,3,2));

        Assertions.assertThrows(CargoItemValidationException.class, () ->
                new CargoItem(9,9,1));

        Assertions.assertThrows(CargoItemValidationException.class, () ->
                new CargoItem(9,1,9));
    }

    @Test
    void createValidCargoItems() {
        CargoItem cargoItem = new CargoItem(1,1,1);
        System.out.println(cargoItem.getName()+"\n");

        cargoItem = new CargoItem(2,2,1);
        System.out.println(cargoItem.getName()+"\n");

        cargoItem = new CargoItem(2,1,2);
        System.out.println(cargoItem.getName()+"\n");

        cargoItem = new CargoItem(3,1,3);
        System.out.println(cargoItem.getName()+"\n");

        cargoItem = new CargoItem(3,3,1);
        System.out.println(cargoItem.getName()+"\n");

        cargoItem = new CargoItem(4,1,4);
        System.out.println(cargoItem.getName()+"\n");

        cargoItem = new CargoItem(4,4,1);
        System.out.println(cargoItem.getName()+"\n");

        cargoItem = new CargoItem(4,2,2);
        System.out.println(cargoItem.getName()+"\n");

        cargoItem = new CargoItem(5,5,1);
        System.out.println(cargoItem.getName()+"\n");

        cargoItem = new CargoItem(5,1,5);
        System.out.println(cargoItem.getName()+"\n");

        cargoItem = new CargoItem(6,3,2);
        System.out.println(cargoItem.getName()+"\n");

        cargoItem = new CargoItem(6,2,3);
        System.out.println(cargoItem.getName()+"\n");

        cargoItem = new CargoItem(6,1,6);
        System.out.println(cargoItem.getName()+"\n");

        cargoItem = new CargoItem(6,6,1);
        System.out.println(cargoItem.getName()+"\n");

        cargoItem = new CargoItem(8,4,2);
        System.out.println(cargoItem.getName()+"\n");

        cargoItem = new CargoItem(8,2,4);
        System.out.println(cargoItem.getName()+"\n");

        cargoItem = new CargoItem(9,3,3);
        System.out.println(cargoItem.getName()+"\n");
    }
}