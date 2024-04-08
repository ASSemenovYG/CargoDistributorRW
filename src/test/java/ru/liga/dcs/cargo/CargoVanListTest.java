package ru.liga.dcs.cargo;

import org.junit.jupiter.api.Test;
import ru.liga.dcs.algorithm.DistributionAlgorithm;
import ru.liga.dcs.algorithm.SingleSortedCargoDistribution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CargoVanListTest {
    private final List<CargoItem> cargoItemsToLoad = new ArrayList<>(Arrays.asList(
            new CargoItem(9, 3, 3),
            new CargoItem(6, 3, 2),
            new CargoItem(6, 2, 3),
            new CargoItem(1, 1, 1),
            new CargoItem(5, 1, 5),
            new CargoItem(4, 1, 4),
            new CargoItem(4, 2, 2)
    ));

    private final CargoList cargoList = new CargoListMock(cargoItemsToLoad);

    private final CargoVanToJsonConverter converter = new CargoVanToJsonConverter(true);

    @Test
    void printCargoVanList() {
        CargoVanList cargoVanList = converter.getLoadedVansFromJsonFile("src/test/resources/testLoadedVans.json");
        cargoVanList.printCargoVanList();
        assertThat(cargoVanList.getCargoVans())
                .hasSize(3);
    }

    @Test
    void getAllCargoItemsFromVans() {
        CargoVanList cargoVanList = converter.getLoadedVansFromJsonFile("src/test/resources/testLoadedVans.json");

        DistributionAlgorithm singleSortedCargoDistribution = new SingleSortedCargoDistribution(cargoList);

        assertThat(cargoVanList.getAllCargoItemsFromVans())
                .containsExactlyInAnyOrderElementsOf(singleSortedCargoDistribution.getAllCargoItemsFromLoadedVans());
    }
}