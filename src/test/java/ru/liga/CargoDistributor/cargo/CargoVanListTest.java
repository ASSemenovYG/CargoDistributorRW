package ru.liga.CargoDistributor.cargo;

import org.junit.jupiter.api.Test;
import ru.liga.CargoDistributor.algorithm.DistributionAlgorithm;
import ru.liga.CargoDistributor.algorithm.SingleSortedCargoDistributionAlgorithm;

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

    private final CargoItemList cargoList = new CargoItemList(cargoItemsToLoad);

    private final FileService fileService = new FileService(true);
    private final CargoConverterService cargoConverterService = new CargoConverterService();

    @Test
    void printCargoVanList() {
        CargoVanList cargoVanList = cargoConverterService.deserializeLoadedVansFromJson(fileService.readFromFile("src/test/resources/test_loaded_vans.json"));
        System.out.println(cargoVanList.getCargoVanListAsString());
        assertThat(cargoVanList.getCargoVans())
                .hasSize(3);
    }

    @Test
    void getAllCargoItemsFromVans() {
        CargoVanList cargoVanList = cargoConverterService.deserializeLoadedVansFromJson(fileService.readFromFile("src/test/resources/test_loaded_vans.json"));

        DistributionAlgorithm singleSortedCargoDistribution = new SingleSortedCargoDistributionAlgorithm();
        CargoVanList cargoVanListSorted = new CargoVanList();
        cargoVanListSorted.distributeCargo(singleSortedCargoDistribution, cargoList);

        assertThat(cargoVanList.getAllCargoItemsFromVans())
                .containsExactlyInAnyOrderElementsOf(cargoVanListSorted.getAllCargoItemsFromVans());
    }
}