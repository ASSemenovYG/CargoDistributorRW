package ru.liga.dcs.algorithm;

import ru.liga.dcs.cargo.CargoItem;
import ru.liga.dcs.cargo.CargoList;
import ru.liga.dcs.cargo.CargoVan;

import java.util.Collection;
import java.util.List;

/**
 * Абстрактный класс алгоритма распределения посылок по грузовым фургонам
 */
public abstract class DistributionAlgorithm {
    private final List<CargoVan> loadedVans;

    /**
     * @param cargoList Список посылок для распределения
     */
    public DistributionAlgorithm(CargoList cargoList) {
        this.loadedVans = distributeCargo(cargoList);
    }

    /**
     * @return Список загруженных машин
     */
    public List<CargoVan> getLoadedVans() {
        return loadedVans;
    }

    /**
     * @return Лист с посылками из всех загруженных фургонов
     */
    public List<CargoItem> getAllCargoItemsFromLoadedVans() {
        return loadedVans.stream()
                .map(CargoVan::getLoadedCargoItems)
                .flatMap(Collection::stream)
                .toList();
    }

    /**
     * Выводит в консоль все загруженные машины
     */
    public void printLoadedVans() {
        for (CargoVan cargoVan : loadedVans) {
            cargoVan.printVanCargo();
            System.out.println("\r");
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