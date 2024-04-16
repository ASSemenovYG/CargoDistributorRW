package ru.liga.CargoDistributor.algorithm;

import ru.liga.CargoDistributor.cargo.CargoItem;
import ru.liga.CargoDistributor.cargo.CargoList;
import ru.liga.CargoDistributor.cargo.CargoVan;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс алгоритма распределения по принципу
 * 1 машина = 1 посылка
 */
public class OneVanOneItemDistribution extends DistributionAlgorithm {

    public OneVanOneItemDistribution(CargoList cargoList) {
        super(cargoList);
    }

    @Override
    public List<CargoVan> distributeCargo(CargoList cargoList) {
        List<CargoVan> result = new ArrayList<>();
        for (CargoItem cargoItem : cargoList.getCargo()) {
            result.add(new CargoVan(cargoItem));
        }
        return result;
    }
}
