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
 * (по одной посылке на погрузочной линии (паллете))
 */
public class SingleSortedCargoDistribution extends DistributionAlgorithm {
    public SingleSortedCargoDistribution(CargoList cargoList) {
        super("SingleSortedCargoDistribution", cargoList);
    }

    @Override
    public List<CargoVan> distributeCargo(CargoList cargoList) {
        List<CargoVan> result = new ArrayList<>();
        LinkedList<CargoItem> cargoItems = new LinkedList<>(cargoList.getCargo());
        cargoItems.sort(Comparator.comparing(CargoItem::getLength).reversed());

        while (!cargoItems.isEmpty()) {
            CargoVan van = new CargoVan();
            for (int i = 0; i < CargoVan.VAN_LENGTH; i++) {
                if (cargoItems.isEmpty()) {
                    break;
                }
                van.addLine(new CargoVan.CargoVanLine(cargoItems.pollFirst()));
                //cargoItems.remove(cargoItem);
            }
            result.add(van);
        }
        return result;
/*        List<CargoItem> cargoItems = cargoList.getCargo();
        cargoItems.sort(Comparator.comparing(CargoItem::getLength).reversed());
        for (CargoItem cargoItem : cargoItems) {
            CargoVan van = new CargoVan();
            for (int i = 0; i < CargoVan.VAN_LENGTH; i++) {
                van.addLine(new CargoVan.CargoVanLine(cargoItem));
                cargoItems.remove(cargoItem);
            }
        }*/
/*        cargoList.getCargo().stream()
                .sorted(Comparator.comparing(CargoItem::getLength).reversed())
                .forEach();*/

    }
}
