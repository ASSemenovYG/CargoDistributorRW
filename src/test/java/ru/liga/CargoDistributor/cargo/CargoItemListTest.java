package ru.liga.CargoDistributor.cargo;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CargoItemListTest {

    @Test
    void isEmptyOrNull_nullList() {
        CargoItemList cargoItemList = new CargoItemList(null);
        assertThat(cargoItemList.isEmptyOrNull()).isTrue();
    }

    @Test
    void isEmptyOrNull_emptyList() {
        CargoItemList cargoItemList = new CargoItemList(new ArrayList<>());
        assertThat(cargoItemList.isEmptyOrNull()).isTrue();
    }

    @Test
    void isEmptyOrNull_nonEmptyList() {
        List<CargoItem> cargoItems = new ArrayList<>();
        cargoItems.add(new CargoItem(1, 1, 1));
        CargoItemList cargoItemList = new CargoItemList(cargoItems);
        assertThat(cargoItemList.isEmptyOrNull()).isFalse();
    }

    @Test
    void getCargoItemNames() {
        List<CargoItem> cargoItems = new ArrayList<>();
        cargoItems.add(new CargoItem(1, 1, 1));
        cargoItems.add(new CargoItem(2, 1, 2));
        cargoItems.add(new CargoItem(3, 1, 3));
        CargoItemList cargoItemList = new CargoItemList(cargoItems);
        assertThat(cargoItemList.getCargoItemNames())
                .contains("1")
                .contains("22")
                .contains("333")
                .hasSize(3);
    }

    @Test
    void getCargoItemNamesAsString() {
        List<CargoItem> cargoItems = new ArrayList<>();
        cargoItems.add(new CargoItem(1, 1, 1));
        cargoItems.add(new CargoItem(2, 1, 2));
        cargoItems.add(new CargoItem(3, 1, 3));
        CargoItemList cargoItemList = new CargoItemList(cargoItems);
        assertThat(cargoItemList.getCargoItemNames())
                .contains("1")
                .contains("22")
                .contains("333");
    }
}