package ru.liga.cargodistributor.cargo;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.liga.cargodistributor.algorithm.CargoDistributionParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляющий собой список посылок для распределения
 */
@Getter
@Component
public class CargoItemList {
    private final List<CargoItem> cargo;

    @Autowired
    public CargoItemList(List<CargoItem> cargo) {
        this.cargo = cargo;
    }

    public CargoItemList(CargoDistributionParameters cargoDistributionParameters) {
        this.cargo = createCargoListFromParameters(cargoDistributionParameters);
    }

    public boolean isEmptyOrNull() {
        if (cargo == null) {
            return true;
        }
        return cargo.isEmpty();
    }

    public List<String> getCargoItemNames() {
        return this.cargo.stream()
                .map(CargoItem::getName)
                .toList();
    }

    public String getCargoItemNamesAsString() {
        StringBuilder sb = new StringBuilder();
        for (CargoItem cargoItem : cargo) {
            sb.append("\n").append(cargoItem.getName()).append("\n");
        }
        return sb.toString();
    }

    private List<CargoItem> createCargoListFromParameters(CargoDistributionParameters cargoDistributionParameters) {
        List<CargoItem> cargoItems = new ArrayList<>();
        List<CargoDistributionParameters.CargoItemToLoad> cargoItemsToLoad = cargoDistributionParameters.getCargoItemsToLoad();

        for (CargoDistributionParameters.CargoItemToLoad cargoItemToLoad : cargoItemsToLoad) {
            for (int i = 0; i < cargoItemToLoad.getCount(); i++) {
                cargoItems.add(cargoItemToLoad.getCargoItem().copy());
            }
        }

        return cargoItems;
    }
}
