package ru.liga.cargodistributor.algorithm;

import org.junit.jupiter.api.Test;
import ru.liga.cargodistributor.cargo.CargoConverterService;
import ru.liga.cargodistributor.cargo.CargoItem;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.CargoVanList;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class OneVanOneItemDistributionAlgorithmServiceTest {
    @Test
    void printLoadedVans() {
        CargoItemList cargoList = new CargoItemList(Arrays.asList(
                new CargoItem(9, 3, 3),
                new CargoItem(6, 3, 2),
                new CargoItem(1, 1, 1),
                new CargoItem(5, 1, 5),
                new CargoItem(4, 1, 4),
                new CargoItem(4, 2, 2)
        ));
        DistributionAlgorithmService oneVanOneItem = new OneVanOneItemDistributionAlgorithmService();
        CargoVanList cargoVanList = new CargoVanList();
        cargoVanList.distributeCargo(oneVanOneItem, cargoList);
        System.out.println(cargoVanList.getCargoVanListAsString(new CargoConverterService()));

        assertThat(cargoVanList.getCargoVans())
                .hasSize(6);
    }
}