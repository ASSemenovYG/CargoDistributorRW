package ru.liga.dcs.algorithm;

import org.junit.jupiter.api.Test;
import ru.liga.dcs.cargo.CargoItem;
import ru.liga.dcs.cargo.CargoList;
import ru.liga.dcs.cargo.CargoListMock;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class OneVanOneItemDistributionTest {
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
        DistributionAlgorithm oneVanOneItem = new OneVanOneItemDistribution(cargoList);
        oneVanOneItem.printLoadedVans();

        assertThat(oneVanOneItem.getLoadedVans())
                .hasSize(6);
    }
}