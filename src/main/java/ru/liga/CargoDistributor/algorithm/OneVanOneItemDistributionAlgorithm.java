package ru.liga.CargoDistributor.algorithm;

import org.springframework.stereotype.Service;
import ru.liga.CargoDistributor.cargo.CargoItem;
import ru.liga.CargoDistributor.cargo.CargoItemList;
import ru.liga.CargoDistributor.cargo.CargoVan;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс алгоритма распределения по принципу
 * 1 машина = 1 посылка
 */
@Service
public class OneVanOneItemDistributionAlgorithm extends DistributionAlgorithm {
    @Override
    public List<CargoVan> distributeCargo(CargoItemList cargoList) {
        List<CargoVan> result = new ArrayList<>();
        for (CargoItem cargoItem : cargoList.getCargo()) {
            result.add(new CargoVan(cargoItem));
        }
        return result;
    }
}
