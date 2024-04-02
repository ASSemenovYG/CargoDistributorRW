package ru.liga.dcs.algorithm;

import ru.liga.dcs.cargo.CargoItem;
import ru.liga.dcs.cargo.CargoList;
import ru.liga.dcs.cargo.CargoVan;

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
        List<CargoItem> cargoItems = cargoList.getCargo();
        for (CargoItem cargoItem : cargoItems) {
            CargoVan van = new CargoVan();
            van.addLine(new CargoVan.CargoVanLine(cargoItem));
            result.add(van);
        }
        return result;
    }
}
