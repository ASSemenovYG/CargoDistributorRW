package ru.liga.CargoDistributor.algorithm;

import org.junit.jupiter.api.Test;
import ru.liga.CargoDistributor.cargo.CargoItem;
import ru.liga.CargoDistributor.cargo.CargoList;
import ru.liga.CargoDistributor.cargo.CargoListMock;
import ru.liga.CargoDistributor.cargo.CargoVanList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SingleSortedCargoDistributionAlgorithmTest {
    @Test
    void printLoadedVans() {
        List<CargoItem> cargoItemsToLoad = new ArrayList<>(Arrays.asList(
                new CargoItem(9, 3, 3),
                new CargoItem(6, 3, 2),
                new CargoItem(6, 2, 3),
                new CargoItem(1, 1, 1),
                new CargoItem(5, 1, 5),
                new CargoItem(4, 1, 4),
                new CargoItem(4, 2, 2)
        ));

        CargoList cargoList = new CargoListMock(cargoItemsToLoad);
        DistributionAlgorithm singleSortedCargoDistribution = new SingleSortedCargoDistributionAlgorithm();
        CargoVanList cargoVanList = new CargoVanList();
        cargoVanList.distributeCargo(singleSortedCargoDistribution, cargoList);
        System.out.println(cargoVanList.getCargoVanListAsString());

        List<CargoItem> loadedCargoItems = new ArrayList<>(cargoVanList.getAllCargoItemsFromVans());

        assertThat(cargoVanList.getCargoVans().size())
                .isEqualTo(3);

        assertThat(loadedCargoItems)
                .containsExactlyInAnyOrderElementsOf(cargoItemsToLoad);
    }
}