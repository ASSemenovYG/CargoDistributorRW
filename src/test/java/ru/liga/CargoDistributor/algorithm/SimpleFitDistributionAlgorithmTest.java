package ru.liga.CargoDistributor.algorithm;

import org.junit.jupiter.api.Test;
import ru.liga.CargoDistributor.cargo.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleFitDistributionAlgorithmTest {
    private final FileService fileService = new FileService(true);
    private final CargoConverterService cargoConverterService = new CargoConverterService();

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

        CargoItemList cargoList = new CargoItemList(cargoItemsToLoad);

        DistributionAlgorithm simpleFitDistribution = new SimpleFitDistributionAlgorithm();
        CargoVanList cargoVanList = new CargoVanList();
        cargoVanList.distributeCargo(simpleFitDistribution, cargoList);
        System.out.println(cargoVanList.getCargoVanListAsString());

        assertThat(cargoVanList.getCargoVans().size())
                .isEqualTo(2);

        assertThat(cargoVanList.getAllCargoItemsFromVans())
                .containsExactlyInAnyOrderElementsOf(cargoItemsToLoad);
    }

    @Test
    void distributeCargo_validCargoFile_oneVan() {
        CargoItemList cargoList = new CargoItemList(cargoConverterService.parseCargoItems(fileService.readFromFile("src/test/resources/test_valid_cargo_file.pkg")));

        DistributionAlgorithm simpleFitDistribution = new SimpleFitDistributionAlgorithm();
        CargoVanList cargoVanList = new CargoVanList();
        cargoVanList.distributeCargo(simpleFitDistribution, cargoList);
        System.out.println(cargoVanList.getCargoVanListAsString());

        assertThat(cargoVanList.getCargoVans().size())
                .isEqualTo(1);

        assertThat(cargoVanList.getAllCargoItemsFromVans())
                .containsExactlyInAnyOrderElementsOf(cargoList.getCargo());
    }

    @Test
    void distributeCargo_validCargoFile1_twoVans() {
        CargoItemList cargoList = new CargoItemList(cargoConverterService.parseCargoItems(fileService.readFromFile("src/test/resources/test_valid_cargo_file_1.pkg")));

        DistributionAlgorithm simpleFitDistribution = new SimpleFitDistributionAlgorithm();
        CargoVanList cargoVanList = new CargoVanList();
        cargoVanList.distributeCargo(simpleFitDistribution, cargoList);
        System.out.println(cargoVanList.getCargoVanListAsString());

        assertThat(cargoVanList.getCargoVans().size())
                .isEqualTo(2);

        assertThat(cargoVanList.getAllCargoItemsFromVans())
                .containsExactlyInAnyOrderElementsOf(cargoList.getCargo());
    }

    @Test
    void distributeCargo_validCargoFile2_twoVans() {
        CargoItemList cargoList = new CargoItemList(cargoConverterService.parseCargoItems(fileService.readFromFile("src/test/resources/test_valid_cargo_file_2.pkg")));

        DistributionAlgorithm simpleFitDistribution = new SimpleFitDistributionAlgorithm();
        CargoVanList cargoVanList = new CargoVanList();
        cargoVanList.distributeCargo(simpleFitDistribution, cargoList);
        System.out.println(cargoVanList.getCargoVanListAsString());

        assertThat(cargoVanList.getCargoVans().size())
                .isEqualTo(2);

        assertThat(cargoVanList.getAllCargoItemsFromVans())
                .containsExactlyInAnyOrderElementsOf(cargoList.getCargo());
    }

    @Test
    void distributeCargo_validCargoFile3_threeVans() {
        CargoItemList cargoList = new CargoItemList(cargoConverterService.parseCargoItems(fileService.readFromFile("src/test/resources/test_valid_cargo_file_3.pkg")));

        DistributionAlgorithm simpleFitDistribution = new SimpleFitDistributionAlgorithm();
        CargoVanList cargoVanList = new CargoVanList();
        cargoVanList.distributeCargo(simpleFitDistribution, cargoList);
        System.out.println(cargoVanList.getCargoVanListAsString());

        assertThat(cargoVanList.getCargoVans().size())
                .isEqualTo(3);

        assertThat(cargoVanList.getAllCargoItemsFromVans())
                .containsExactlyInAnyOrderElementsOf(cargoList.getCargo());
    }
}