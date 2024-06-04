package ru.liga.cargodistributor.algorithm;

import ru.liga.cargodistributor.algorithm.enums.DistributionAlgorithmName;
import ru.liga.cargodistributor.cargo.CargoItem;
import ru.liga.cargodistributor.cargo.CargoVan;

import java.util.LinkedList;

public class CargoDistributionParameters {
    private CargoVan cargoVan;
    private final LinkedList<CargoItemToLoad> cargoItemsToLoad;
    private int vanLimit;
    private DistributionAlgorithmName algorithmName;

    public CargoDistributionParameters(CargoVan cargoVan) {
        this.cargoVan = cargoVan;
        this.cargoItemsToLoad = new LinkedList<>();
    }

    public CargoVan getCargoVan() {
        return cargoVan;
    }

    public void setCargoVan(CargoVan cargoVan) {
        this.cargoVan = cargoVan;
    }

    public LinkedList<CargoItemToLoad> getCargoItemsToLoad() {
        return cargoItemsToLoad;
    }

    public void addCargoItemToLoad(CargoItemToLoad cargoItemToLoad) {
        cargoItemsToLoad.add(cargoItemToLoad);
    }

    public int getVanLimit() {
        return vanLimit;
    }

    public void setVanLimit(int vanLimit) {
        this.vanLimit = vanLimit;
    }

    public DistributionAlgorithmName getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(DistributionAlgorithmName algorithmName) {
        this.algorithmName = algorithmName;
    }

    public String getCargoItemsToLoadStringDescription() {
        StringBuilder result = new StringBuilder();
        for (CargoItemToLoad cargoItemToLoad : cargoItemsToLoad) {
            result.append(cargoItemToLoad.toString()).append("\n");
        }
        return result.toString();
    }

    public boolean isItemWithLegendAlreadyAdded(String legend) {
        return cargoItemsToLoad.stream()
                .map(CargoItemToLoad::getCargoItem)
                .map(CargoItem::getLegend)
                .anyMatch(s -> s.equals(legend));
    }

    public static class CargoItemToLoad {
        private final CargoItem cargoItem;
        private int count;

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

        public void setCount(int count) {
            this.count = count;
        }

        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append("Название типа посылки: ")
                    .append(cargoItem.getName())
                    .append("\n")
                    .append("Количество: ")
                    .append(count)
                    .append("\n")
                    .append("Легенда: ")
                    .append(cargoItem.getLegend())
                    .append("\n");
            return result.toString();
        }
    }
}
