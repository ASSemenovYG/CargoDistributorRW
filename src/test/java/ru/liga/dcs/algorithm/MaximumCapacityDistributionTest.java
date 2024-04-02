package ru.liga.dcs.algorithm;

import org.junit.jupiter.api.Test;
import ru.liga.dcs.cargo.CargoItem;
import ru.liga.dcs.cargo.CargoList;
import ru.liga.dcs.cargo.CargoListFromFile;
import ru.liga.dcs.cargo.CargoListMock;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MaximumCapacityDistributionTest {

    @Test
    void printLoadedVans_oneFullyLoadedVan() {
        CargoList cargoList = new CargoListMock(Arrays.asList(
                new CargoItem("999"),
                new CargoItem("666"),
                new CargoItem("1"),
                new CargoItem("666666"),
                new CargoItem("666666"),
                new CargoItem("55555"),
                new CargoItem("4444"),
                new CargoItem("333"),
                new CargoItem("333"),
                new CargoItem("22")
        ));
        DistributionAlgorithm maximumCapacityDistribution = new MaximumCapacityDistribution(cargoList);
        maximumCapacityDistribution.printLoadedVans();
        assertThat(maximumCapacityDistribution.getLoadedVans().size()).isEqualTo(1);
    }

    @Test
    void printLoadedVans_twoFullyLoadedVans() {
        CargoList cargoList = new CargoListMock(Arrays.asList(
                new CargoItem("999"),
                new CargoItem("666"),
                new CargoItem("1"),
                new CargoItem("666666"),
                new CargoItem("666666"),
                new CargoItem("55555"),
                new CargoItem("4444"),
                new CargoItem("333"),
                new CargoItem("333"),
                new CargoItem("22"),
                new CargoItem("1")
        ));
        DistributionAlgorithm maximumCapacityDistribution = new MaximumCapacityDistribution(cargoList);
        maximumCapacityDistribution.printLoadedVans();
        assertThat(maximumCapacityDistribution.getLoadedVans().size()).isEqualTo(2);
    }

    @Test
    void printLoadedVans_realFile() {
        CargoList cargoList = new CargoListFromFile("src/test/java/ru/liga/dcs/cargo/testValidCargoFile");
        DistributionAlgorithm maximumCapacityDistribution = new MaximumCapacityDistribution(cargoList);
        maximumCapacityDistribution.printLoadedVans();
        assertThat(maximumCapacityDistribution.getLoadedVans().size()).isEqualTo(1);
    }
}