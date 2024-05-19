package ru.liga.cargodistributor.algorithm.serviceImpls;

import org.junit.jupiter.api.Test;
import ru.liga.cargodistributor.algorithm.services.DistributionAlgorithmService;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.cargo.CargoItem;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.CargoVanList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SingleSortedCargoDistributionAlgorithmServiceTest {
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

        CargoItemList cargoList = new CargoItemList(cargoItemsToLoad);
        DistributionAlgorithmService singleSortedCargoDistribution = new SingleSortedCargoDistributionAlgorithmService();
        CargoVanList cargoVanList = new CargoVanList();
        cargoVanList.distributeCargo(singleSortedCargoDistribution, cargoList);
        System.out.println(cargoVanList.getCargoVanListAsString(new CargoConverterService()));

        List<CargoItem> loadedCargoItems = new ArrayList<>(cargoVanList.getAllCargoItemsFromVans());

        assertThat(cargoVanList.getCargoVans().size())
                .isEqualTo(3);

        assertThat(loadedCargoItems)
                .containsExactlyInAnyOrderElementsOf(cargoItemsToLoad);
    }
}