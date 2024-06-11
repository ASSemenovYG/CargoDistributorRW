package ru.liga.cargodistributor.algorithm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.liga.cargodistributor.algorithm.enums.DistributionAlgorithmName;
import ru.liga.cargodistributor.algorithm.exceptions.CargoItemListHasDuplicateLegendsException;
import ru.liga.cargodistributor.cargo.CargoItem;
import ru.liga.cargodistributor.cargo.CargoVan;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
@AllArgsConstructor
public class CargoDistributionParameters {
    @Setter
    private CargoVan cargoVan;
    private final LinkedList<CargoItemToLoad> cargoItemsToLoad;
    @Setter
    private int vanLimit;
    @Setter
    private DistributionAlgorithmName algorithmName;

    public CargoDistributionParameters(CargoVan cargoVan) {
        this.cargoVan = cargoVan;
        this.cargoItemsToLoad = new LinkedList<>();
    }

    public void addCargoItemToLoad(CargoItemToLoad cargoItemToLoad) {
        cargoItemsToLoad.add(cargoItemToLoad);
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

    public void validateItemListForDuplicateLegends() {
        List<CargoItem> cargoItemsWithDuplicateLegends = findDuplicateLegendsInItemList();
        if (!cargoItemsWithDuplicateLegends.isEmpty()) {
            throw new CargoItemListHasDuplicateLegendsException(createDuplicateLegendsErrorMessage(cargoItemsWithDuplicateLegends));
        }
    }

    private List<CargoItem> findDuplicateLegendsInItemList() {
        List<String> legends = cargoItemsToLoad.stream()
                .map(CargoItemToLoad::getCargoItem)
                .map(CargoItem::getLegend)
                .toList();

        List<String> duplicateLegends = legends.stream()
                .filter(s -> Collections.frequency(legends, s) > 1)
                .toList();

        return cargoItemsToLoad.stream()
                .map(CargoItemToLoad::getCargoItem)
                .filter(cargoItem -> duplicateLegends.contains(cargoItem.getLegend()))
                .toList();
    }

    private String createDuplicateLegendsErrorMessage(List<CargoItem> cargoItemsWithDuplicateLegends) {
        StringBuilder validationMessage = new StringBuilder("Following Cargo Items In Request Have Duplicated Legends, please set parameter overrideLegend for them or change item list: ");
        for (CargoItem duplicate : cargoItemsWithDuplicateLegends) {
            validationMessage.append("name: ").append(duplicate.getName()).append(", legend: ").append(duplicate.getLegend()).append("; ");
        }
        return validationMessage.toString();
    }

    @Getter
    public static class CargoItemToLoad {
        private final CargoItem cargoItem;
        @Setter
        private int count;

        public CargoItemToLoad(CargoItem cargoItem, int count) {
            this.cargoItem = cargoItem;
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
