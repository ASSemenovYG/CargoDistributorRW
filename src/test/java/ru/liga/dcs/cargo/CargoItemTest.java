package ru.liga.dcs.cargo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class CargoItemTest {

    @Test
    void createCargoItemsWithIncorrectParameters() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new CargoItem(0,1,1));

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new CargoItem(1,0,1));

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new CargoItem(1,1,0));

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new CargoItem(-100,0,100));

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new CargoItem(10,5,5));

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new CargoItem(9,3,2));

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new CargoItem(9,9,1));

        Assertions.assertThrows(IllegalArgumentException.class, () ->
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

    @Test
    @Disabled("Deprecated constructor")
    @Deprecated
    void createCargoItemWithEmptyName() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new CargoItem(""));
    }

    @Test
    @Disabled("Deprecated constructor")
    @Deprecated
    void createCargoItemWithNameLengthGreaterThanSix() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new CargoItem("999999999999999"));
    }

    @Test
    @Disabled("Deprecated constructor")
    @Deprecated
    void createCargoItemWithNameMaxLength() {
        CargoItem cargoItem = new CargoItem("666666");
        assertThat(cargoItem.getLength()).isEqualTo(6);
    }

    @Test
    @Disabled("Deprecated constructor")
    @Deprecated
    void createCargoItemWithNameMinLength() {
        CargoItem cargoItem = new CargoItem("1");
        assertThat(cargoItem.getLength()).isEqualTo(1);
    }
}