package ru.liga.cargodistributor.algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.liga.cargodistributor.cargo.CargoItem;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.CargoVan;

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
@Service
public class SingleSortedCargoDistributionAlgorithmService extends DistributionAlgorithmService {
    private static final Logger LOGGER = LogManager.getLogger(SingleSortedCargoDistributionAlgorithmService.class);

    @Override
    public List<CargoVan> distributeCargo(CargoItemList cargoList) {
        List<CargoVan> result = new ArrayList<>();
        LinkedList<CargoItem> cargoItems = new LinkedList<>(cargoList.getCargo());
        cargoItems.sort(Comparator.comparing(CargoItem::getWidth).reversed().thenComparing(CargoItem::getLength));

        while (!cargoItems.isEmpty()) {
            LOGGER.debug("Создаю новый грузовой фургон");
            CargoVan van = new CargoVan();
            boolean vanIsNotFull = true;
            int currentLengthCoordinate;
            int nextLengthCoordinate = 0;
            while (vanIsNotFull) {
                CargoItem cargoItem = cargoItems.peekFirst();
                if (cargoItem != null) {
                    LOGGER.debug("Обработка посылки:\n{}", cargoItem.getName());
                    currentLengthCoordinate = nextLengthCoordinate;
                    LOGGER.debug("Попытка установить посылку на строку {}", currentLengthCoordinate);
                    vanIsNotFull = van.tryPuttingCargoItemAtCoordinates(cargoItem, currentLengthCoordinate, 0);
                    LOGGER.debug(vanIsNotFull ? "Посылка успешно добавлена в фургон" : "Не удалось загрузить посылку в грузовой фургон");
                    if (vanIsNotFull) {
                        nextLengthCoordinate = currentLengthCoordinate + cargoItem.getLength();
                        LOGGER.debug("Номер строки для добавления следующей посылки {}", nextLengthCoordinate);
                        cargoItems.removeFirst();
                    }
                } else {
                    LOGGER.debug("Перехожу к следующему фургону");
                    break;
                }
            }
            LOGGER.debug("Добавляю фургон в список загруженных");
            result.add(van);
        }
        LOGGER.info("Количество загруженных фургонов: {}", result.size());
        return result;
    }
}
