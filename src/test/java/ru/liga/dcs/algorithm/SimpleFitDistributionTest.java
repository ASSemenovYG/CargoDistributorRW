package ru.liga.dcs.algorithm;

import org.junit.jupiter.api.Test;
import ru.liga.dcs.cargo.CargoItem;
import ru.liga.dcs.cargo.CargoList;
import ru.liga.dcs.cargo.CargoListFromFile;
import ru.liga.dcs.cargo.CargoListMock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleFitDistributionTest {

    @Test
    void distributeCargo_TwoVans() {
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

        DistributionAlgorithm simpleFitDistribution = new SimpleFitDistribution(cargoList);
        simpleFitDistribution.printLoadedVans();

        assertThat(simpleFitDistribution.getLoadedVans().size())
                .isEqualTo(2);

        assertThat(simpleFitDistribution.getAllCargoItemsFromLoadedVans())
                .containsExactlyInAnyOrderElementsOf(cargoItemsToLoad);
    }

    @Test
    void distributeCargo_validCargoFile_oneVan() {
        CargoList cargoList = new CargoListFromFile("src/test/resources/testValidCargoFile");

        DistributionAlgorithm simpleFitDistribution = new SimpleFitDistribution(cargoList);
        simpleFitDistribution.printLoadedVans();

        assertThat(simpleFitDistribution.getLoadedVans().size())
                .isEqualTo(1);

        assertThat(simpleFitDistribution.getAllCargoItemsFromLoadedVans())
                .containsExactlyInAnyOrderElementsOf(cargoList.getCargo());
    }

    @Test
    void distributeCargo_validCargoFile1_twoVans() {
        CargoList cargoList = new CargoListFromFile("src/test/resources/testValidCargoFile1");

        DistributionAlgorithm simpleFitDistribution = new SimpleFitDistribution(cargoList);
        simpleFitDistribution.printLoadedVans();

        assertThat(simpleFitDistribution.getLoadedVans().size())
                .isEqualTo(2);

        assertThat(simpleFitDistribution.getAllCargoItemsFromLoadedVans())
                .containsExactlyInAnyOrderElementsOf(cargoList.getCargo());
    }

    @Test
    void distributeCargo_validCargoFile2_twoVans() {
        CargoList cargoList = new CargoListFromFile("src/test/resources/testValidCargoFile2");

        DistributionAlgorithm simpleFitDistribution = new SimpleFitDistribution(cargoList);
        simpleFitDistribution.printLoadedVans();

        assertThat(simpleFitDistribution.getLoadedVans().size())
                .isEqualTo(2);

        assertThat(simpleFitDistribution.getAllCargoItemsFromLoadedVans())
                .containsExactlyInAnyOrderElementsOf(cargoList.getCargo());
    }

    @Test
    void distributeCargo_validCargoFile3_threeVans() {
        CargoList cargoList = new CargoListFromFile("src/test/resources/testValidCargoFile3");

        DistributionAlgorithm simpleFitDistribution = new SimpleFitDistribution(cargoList);
        simpleFitDistribution.printLoadedVans();

        assertThat(simpleFitDistribution.getLoadedVans().size())
                .isEqualTo(3);

        assertThat(simpleFitDistribution.getAllCargoItemsFromLoadedVans())
                .containsExactlyInAnyOrderElementsOf(cargoList.getCargo());
    }
}