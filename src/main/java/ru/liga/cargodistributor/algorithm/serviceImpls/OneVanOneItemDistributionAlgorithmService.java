package ru.liga.cargodistributor.algorithm.serviceImpls;

import org.springframework.stereotype.Service;
import ru.liga.cargodistributor.algorithm.CargoDistributionParameters;
import ru.liga.cargodistributor.algorithm.services.DistributionAlgorithmService;
import ru.liga.cargodistributor.cargo.CargoItem;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.CargoVan;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс алгоритма распределения по принципу
 * 1 машина = 1 посылка
 */
@Service
public class OneVanOneItemDistributionAlgorithmService extends DistributionAlgorithmService {
    @Override
    public List<CargoVan> distributeCargo(CargoItemList cargoList) {
        return processCargoListAndDistribute(new CargoVan(), cargoList);
    }

    @Override
    public List<CargoVan> distributeCargoByParameters(CargoDistributionParameters cargoDistributionParameters) {
        //todo: add tests for this method
        return processCargoListAndDistribute(cargoDistributionParameters.getCargoVan(), new CargoItemList(cargoDistributionParameters));
    }

    private List<CargoVan> processCargoListAndDistribute(CargoVan cargoVanType, CargoItemList cargoList) {
        List<CargoVan> result = new ArrayList<>();
        for (CargoItem cargoItem : cargoList.getCargo()) {
            result.add(new CargoVan(cargoVanType.getLength(), cargoVanType.getWidth(), cargoItem));
        }
        return result;
    }
}
