package ru.liga.cargodistributor.algorithm;

import ru.liga.cargodistributor.algorithm.enums.DistributionAlgorithmName;
import ru.liga.cargodistributor.cargo.CargoItem;
import ru.liga.cargodistributor.cargo.CargoVan;

import java.util.ArrayList;
import java.util.List;

public class CargoDistributionParameters {
    private CargoVan cargoVan;
    private final List<CargoItemToLoad> cargoItemsToLoad;
    private int vanCount;
    private DistributionAlgorithmName algorithmName;

    public CargoDistributionParameters(CargoVan cargoVan) {
        this.cargoVan = cargoVan;
        this.cargoItemsToLoad = new ArrayList<>();
    }

    public CargoVan getCargoVan() {
        return cargoVan;
    }

    public void setCargoVan(CargoVan cargoVan) {
        this.cargoVan = cargoVan;
    }

    public List<CargoItemToLoad> getCargoItemsToLoad() {
        return cargoItemsToLoad;
    }

    public void addCargoItemToLoad(CargoItemToLoad cargoItemToLoad) {
        cargoItemsToLoad.add(cargoItemToLoad);
    }

    public int getVanCount() {
        return vanCount;
    }

    public void setVanCount(int vanCount) {
        this.vanCount = vanCount;
    }

    public DistributionAlgorithmName getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(DistributionAlgorithmName algorithmName) {
        this.algorithmName = algorithmName;
    }

    public static class CargoItemToLoad {
        private final CargoItem cargoItem;
        private final int count;

        public CargoItemToLoad(CargoItem cargoItem, int count) {
            this.cargoItem = cargoItem;
            this.count = count;
        }

        public CargoItem getCargoItem() {
            return cargoItem;
        }

        public int getCount() {
            return count;
        }
    }
}
