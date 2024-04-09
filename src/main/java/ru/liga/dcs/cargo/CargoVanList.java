package ru.liga.dcs.cargo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Оберточный класс для сериализации/десериализации списка грузовых фургонов
 */
@JsonAutoDetect
public class CargoVanList {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    private final List<CargoVan> cargoVans;

    /**
     * Конструктор для десериализации
     */
    public CargoVanList() {
        this.cargoVans = new ArrayList<>();
    }

    public CargoVanList(List<CargoVan> cargoVans) {
        this.cargoVans = cargoVans;
    }

    public List<CargoVan> getCargoVans() {
        return cargoVans;
    }

    public boolean isListSizeLessOrEqualThanMaxSize(int maxSize) {
        return cargoVans.size() <= maxSize;
    }

    /**
     * @return Список названий всех посылок во всех фургонах
     */
    public List<String> getAllCargoItemNames() {
        return cargoVans.stream()
                .map(CargoVan::getLoadedCargoItems)
                .flatMap(Collection::stream)
                .map(CargoItem::getName)
                .toList();
    }

    public void printAllCargoItems() {
        for (CargoItem cargoItem : cargoVans.stream()
                .map(CargoVan::getLoadedCargoItems)
                .flatMap(Collection::stream)
                .toList()
        ) {
            System.out.println(cargoItem.getName());
            System.out.println("\r");
        }
    }

    /**
     * Выводит в консоль все загруженные машины
     */
    public void printCargoVanList() {
        setAllCargoByCoordinates();
        for (CargoVan cargoVan : cargoVans) {
            cargoVan.printVanCargo();
            System.out.println("\r");
        }
    }

    /**
     * @return Лист с посылками из всех фургонов в списке
     */
    public List<CargoItem> getAllCargoItemsFromVans() {
        return cargoVans.stream()
                .map(CargoVan::getLoadedCargoItems)
                .flatMap(Collection::stream)
                .toList();
    }

    private void setAllCargoByCoordinates() {
        for (CargoVan cargoVan : cargoVans) {
            cargoVan.initializeCargo();
            for (CargoItem cargoItem : cargoVan.getLoadedCargoItems()) {
                for (CargoItem.Coordinates coordinates : cargoItem.getCoordinates()) {
                    cargoVan.fillExactCargoVanCellByCoordinate(cargoItem, coordinates);
                }
            }
        }
    }
}
