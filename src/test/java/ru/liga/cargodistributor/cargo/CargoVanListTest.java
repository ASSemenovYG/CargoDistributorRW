package ru.liga.cargodistributor.cargo;

import org.junit.jupiter.api.Test;
import ru.liga.cargodistributor.algorithm.services.DistributionAlgorithmService;
import ru.liga.cargodistributor.algorithm.serviceImpls.SingleSortedCargoDistributionAlgorithmService;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

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
        CargoVanList cargoVanList = cargoConverterService.deserializeLoadedVansFromJson(fileService.readFromFileByPath("src/test/resources/test_loaded_vans.json"));
        System.out.println(cargoVanList.getCargoVanListAsString(cargoConverterService));
        assertThat(cargoVanList.getCargoVans())
                .hasSize(3);
    }

    @Test
    void getAllCargoItemsFromVans() {
        CargoVanList cargoVanList = cargoConverterService.deserializeLoadedVansFromJson(fileService.readFromFileByPath("src/test/resources/test_loaded_vans.json"));

        DistributionAlgorithmService singleSortedCargoDistribution = new SingleSortedCargoDistributionAlgorithmService();
        CargoVanList cargoVanListSorted = new CargoVanList();
        cargoVanListSorted.distributeCargo(singleSortedCargoDistribution, cargoList);

        assertThat(cargoVanList.getAllCargoItemsFromVans())
                .containsExactlyInAnyOrderElementsOf(cargoVanListSorted.getAllCargoItemsFromVans());
    }
}