package ru.liga.dcs.algorithm;

import org.junit.jupiter.api.Test;
import ru.liga.dcs.cargo.CargoItem;
import ru.liga.dcs.cargo.CargoList;
import ru.liga.dcs.cargo.CargoListMock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SingleSortedCargoDistributionTest {
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
        DistributionAlgorithm singleSortedCargoDistribution = new SingleSortedCargoDistribution(cargoList);
        singleSortedCargoDistribution.printLoadedVans();

        List<CargoItem> loadedCargoItems = new ArrayList<>(singleSortedCargoDistribution.getAllCargoItemsFromLoadedVans());

        assertThat(singleSortedCargoDistribution.getLoadedVans().size())
                .isEqualTo(3);

        assertThat(loadedCargoItems)
                .containsExactlyInAnyOrderElementsOf(cargoItemsToLoad);
    }
}