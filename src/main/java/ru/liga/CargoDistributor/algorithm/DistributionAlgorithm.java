package ru.liga.CargoDistributor.algorithm;

import org.springframework.stereotype.Service;
import ru.liga.CargoDistributor.cargo.CargoItemList;
import ru.liga.CargoDistributor.cargo.CargoVan;

import java.util.List;

/**
 * Абстрактный класс алгоритма распределения посылок по грузовым фургонам
 */
@Service
public abstract class DistributionAlgorithm {
    /**
     * Метод, реализующий алгоритм распределения посылок по фургонам
     *
     * @param cargoList Список посылок
     * @return Список загруженных фургонов
     */
    public abstract List<CargoVan> distributeCargo(CargoItemList cargoList);
}