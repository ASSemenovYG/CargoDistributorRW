package ru.liga.dcs.cargo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CargoItemTest {

    @Test
    void createCargoItemWithNullName() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new CargoItem(null));
    }

    @Test
    void createCargoItemWithEmptyName() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new CargoItem(""));
    }

    @Test
    void createCargoItemWithNameLengthGreaterThanSix() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new CargoItem("999999999999999"));
    }

    @Test
    void createCargoItemWithNameMaxLength() {
        CargoItem cargoItem = new CargoItem("666666");
        assertThat(cargoItem.getLength()).isEqualTo(6);
    }

    @Test
    void createCargoItemWithNameMinLength() {
        CargoItem cargoItem = new CargoItem("1");
        assertThat(cargoItem.getLength()).isEqualTo(1);
    }

}