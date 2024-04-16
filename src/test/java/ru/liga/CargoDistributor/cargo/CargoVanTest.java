package ru.liga.CargoDistributor.cargo;

import org.assertj.core.data.Index;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CargoVanTest {
    @Test
    void printVanCargo() {
        CargoItem cargoItem = new CargoItem(9, 3, 3);
        CargoVan.CargoVanCell testCell = new CargoVan.CargoVanCell(cargoItem);
        CargoVan.CargoVanCell[] testCellLine = new CargoVan.CargoVanCell[]{
                testCell,
                testCell,
                testCell,
                new CargoVan.CargoVanCell(),
                new CargoVan.CargoVanCell(),
                new CargoVan.CargoVanCell()
        };
        CargoVan.CargoVanCell[] testEmptyCellLine = new CargoVan.CargoVanCell[]{
                new CargoVan.CargoVanCell(),
                new CargoVan.CargoVanCell(),
                new CargoVan.CargoVanCell(),
                new CargoVan.CargoVanCell(),
                new CargoVan.CargoVanCell(),
                new CargoVan.CargoVanCell()
        };
        CargoVan van = new CargoVan(cargoItem);
        System.out.println(van.getVanCargoAsString());

        assertThat(van.getCargo())
                .isNotEmpty()
                .hasDimensions(6, 6)
                .contains(testCellLine, Index.atIndex(0))
                .contains(testCellLine, Index.atIndex(1))
                .contains(testCellLine, Index.atIndex(2))
                .contains(testEmptyCellLine, Index.atIndex(3))
                .contains(testEmptyCellLine, Index.atIndex(4))
                .contains(testEmptyCellLine, Index.atIndex(5));
    }
}