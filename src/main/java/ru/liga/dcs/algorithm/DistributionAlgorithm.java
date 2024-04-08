package ru.liga.dcs.algorithm;

import ru.liga.dcs.cargo.CargoItem;
import ru.liga.dcs.cargo.CargoList;
import ru.liga.dcs.cargo.CargoVan;
import ru.liga.dcs.cargo.CargoVanList;

import java.util.List;

/**
 * Абстрактный класс алгоритма распределения посылок по грузовым фургонам
 */
public abstract class DistributionAlgorithm {
    private final CargoVanList loadedVans;

    /**
     * @param cargoList Список посылок для распределения
     */
    public DistributionAlgorithm(CargoList cargoList) {
        this.loadedVans = new CargoVanList(distributeCargo(cargoList));
        fillCoordinatesForLoadedCargoItems();
    }

    public CargoVanList getLoadedVansAsObject() {
        return loadedVans;
    }

    /**
     * @return Список загруженных машин
     */
    public List<CargoVan> getLoadedVans() {
        return loadedVans.getCargoVans();
    }

    /**
     * @return Лист с посылками из всех загруженных фургонов
     */
    public List<CargoItem> getAllCargoItemsFromLoadedVans() {
        return loadedVans.getAllCargoItemsFromVans();
    }

    /**
     * Выводит в консоль все загруженные машины
     */
    public void printLoadedVans() {
        loadedVans.printCargoVanList();
    }

    private void fillCoordinatesForLoadedCargoItems() {
        for (CargoVan cargoVan : loadedVans.getCargoVans()) {
            for (CargoItem cargoItem : cargoVan.getLoadedCargoItems()) {
                cargoItem.fillCoordinatesByCargoVan(cargoVan);
            }
        }
    }

    /**
     * Метод, реализующий алгоритм распределения посылок по фургонам
     *
     * @param cargoList Список посылок
     * @return Список загруженных фургонов
     */
    public abstract List<CargoVan> distributeCargo(CargoList cargoList);
    //TODO: Переписать алгоритмы, с учетом обновленного парсера посылок
}