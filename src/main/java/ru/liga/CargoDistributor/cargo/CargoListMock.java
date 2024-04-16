package ru.liga.CargoDistributor.cargo;

import java.util.List;

/**
 * Класс, реализующий интерфейс {@link CargoList},
 * позволяющий получать список посылок для распределения на вход, а не из файла
 */
public class CargoListMock implements CargoList {
    private final List<CargoItem> cargo;

    public CargoListMock(List<CargoItem> cargo) {
        this.cargo = cargo;
    }

    public List<CargoItem> getCargo() {
        return cargo;
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
}