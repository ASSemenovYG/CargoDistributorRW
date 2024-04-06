package ru.liga.dcs.algorithm;

import ru.liga.dcs.cargo.CargoItem;
import ru.liga.dcs.cargo.CargoList;
import ru.liga.dcs.cargo.CargoVan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Класс алгоритма, реализующего наполнение грузовика после одноразовой сортировки
 * <br>по максимальной ширине (сначала более широкие)
 * <br>и минимальной длине (сначала менее длинные)
 * <br>Посылки ставятся друг на друга, пока есть место в грузовом фургоне, если нет - переход к следующему фургону
 */
public class SingleSortedCargoDistribution extends DistributionAlgorithm {
    public SingleSortedCargoDistribution(CargoList cargoList) {
        super(cargoList);
    }

    @Override
    public List<CargoVan> distributeCargo(CargoList cargoList) {
        List<CargoVan> result = new ArrayList<>();
        LinkedList<CargoItem> cargoItems = new LinkedList<>(cargoList.getCargo());
        cargoItems.sort(Comparator.comparing(CargoItem::getWidth).reversed().thenComparing(CargoItem::getLength));

        while (!cargoItems.isEmpty()) {
            CargoVan van = new CargoVan();
            boolean vanIsNotFull = true;
            int currentLengthCoordinate;
            int nextLengthCoordinate = 0;
            while (vanIsNotFull) {
                CargoItem cargoItem = cargoItems.peekFirst();
                if (cargoItem != null) {
                    currentLengthCoordinate = nextLengthCoordinate;
                    vanIsNotFull = van.tryPuttingCargoItemAtCoordinates(cargoItem, currentLengthCoordinate, 0);
                    if (vanIsNotFull) {
                        nextLengthCoordinate = currentLengthCoordinate + cargoItem.getLength();
                        cargoItems.removeFirst();
                    }
                } else {
                    break;
                }
            }
            result.add(van);
        }
        return result;
    }
}
