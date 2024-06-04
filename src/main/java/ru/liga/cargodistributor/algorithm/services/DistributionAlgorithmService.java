package ru.liga.cargodistributor.algorithm.services;

import org.springframework.stereotype.Service;
import ru.liga.cargodistributor.algorithm.CargoDistributionParameters;
import ru.liga.cargodistributor.algorithm.enums.DistributionAlgorithmName;
import ru.liga.cargodistributor.algorithm.serviceImpls.OneVanOneItemDistributionAlgorithmService;
import ru.liga.cargodistributor.algorithm.serviceImpls.SimpleFitDistributionAlgorithmService;
import ru.liga.cargodistributor.algorithm.serviceImpls.SingleSortedCargoDistributionAlgorithmService;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.CargoVan;

import java.util.List;

/**
 * Абстрактный класс алгоритма распределения посылок по грузовым фургонам
 */
@Service
public abstract class DistributionAlgorithmService {
    /**
     * Метод, реализующий алгоритм распределения посылок по фургонам
     *
     * @param cargoList Список посылок
     * @return Список загруженных фургонов
     */
    public abstract List<CargoVan> distributeCargo(CargoItemList cargoList);

    /**
     * Метод, реализующий алгоритм распределения посылок по параметрам (тип фургона и типы посылок)
     *
     * @param cargoDistributionParameters Список посылок
     * @return Список загруженных фургонов
     */
    public abstract List<CargoVan> distributeCargoByParameters(CargoDistributionParameters cargoDistributionParameters);

    public static DistributionAlgorithmService createServiceByName(DistributionAlgorithmName algorithmName) {
        return switch (algorithmName) {
            case SIMPLE_FIT -> new SimpleFitDistributionAlgorithmService();
            case ONE_VAN_ONE_ITEM -> new OneVanOneItemDistributionAlgorithmService();
            case SINGLE_SORTED -> new SingleSortedCargoDistributionAlgorithmService();
        };
    }
}