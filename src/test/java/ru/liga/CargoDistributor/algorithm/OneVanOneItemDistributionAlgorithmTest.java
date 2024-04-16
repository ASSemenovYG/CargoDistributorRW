package ru.liga.CargoDistributor.algorithm;

import org.junit.jupiter.api.Test;
import ru.liga.CargoDistributor.cargo.CargoItem;
import ru.liga.CargoDistributor.cargo.CargoList;
import ru.liga.CargoDistributor.cargo.CargoListMock;
import ru.liga.CargoDistributor.cargo.CargoVanList;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class OneVanOneItemDistributionAlgorithmTest {
    @Test
    void printLoadedVans() {
        CargoList cargoList = new CargoListMock(Arrays.asList(
                new CargoItem(9, 3, 3),
                new CargoItem(6, 3, 2),
                new CargoItem(1, 1, 1),
                new CargoItem(5, 1, 5),
                new CargoItem(4, 1, 4),
                new CargoItem(4, 2, 2)
        ));
        DistributionAlgorithm oneVanOneItem = new OneVanOneItemDistributionAlgorithm();
        CargoVanList cargoVanList = new CargoVanList();
        cargoVanList.distributeCargo(oneVanOneItem, cargoList);
        System.out.println(cargoVanList.getCargoVanListAsString());

        assertThat(cargoVanList.getCargoVans())
                .hasSize(6);
    }
}