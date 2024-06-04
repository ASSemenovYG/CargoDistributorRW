package ru.liga.cargodistributor.algorithm.serviceImpls;

import com.googlecode.caparf.algorithms.spp2d.SimpleFit;
import com.googlecode.caparf.framework.base.Algorithm;
import com.googlecode.caparf.framework.items.Rectangle;
import com.googlecode.caparf.framework.items.RectanglePlacement;
import com.googlecode.caparf.framework.spp2d.Input;
import com.googlecode.caparf.framework.spp2d.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.liga.cargodistributor.algorithm.CargoDistributionParameters;
import ru.liga.cargodistributor.algorithm.services.DistributionAlgorithmService;
import ru.liga.cargodistributor.cargo.CargoItem;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.CargoVan;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, реализующий алгоритм распределения SimpleFit
 * <br>
 * Представляет собой алгоритм решения задачи <a href="https://en.wikipedia.org/wiki/Strip_packing_problem">Strip Packing Problem</a>
 * <br>
 * Source алгоритма SimpleFit взят <a href="https://github.com/denisnsc/caparf">отсюда</a>
 * <br>
 * До тех пор, пока есть нераспределенные посылки, вызывается алгоритм SimpleFit
 * <br>
 * Из посылок, не вылезающих за верхнюю границу фургона собирается загруженный фургон, загруженные в фургон посылки исключаются из списка нераспределенных
 */
@Service
public class SimpleFitDistributionAlgorithmService extends DistributionAlgorithmService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFitDistributionAlgorithmService.class);

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
        List<CargoItem> processingCargoList = new ArrayList<>(cargoList.getCargo());
        while (!processingCargoList.isEmpty()) {
            LOGGER.debug("Запускаю формирование следующего фургона");
            result.add(getNextFilledVan(processingCargoList, cargoVanType));
        }
        LOGGER.info("Распределение посылок завершено. Итоговое количество фургонов: {}", result.size());
        return result;
    }

    private CargoVan getNextFilledVan(List<CargoItem> cargoItems, CargoVan cargoVanType) {
        Algorithm<Input, Output> algorithm = new SimpleFit(SimpleFit.ItemOrder.FIRST_FIT, SimpleFit.PlacementStrategy.DEFAULT);
        List<Rectangle> rectangles = new ArrayList<>();

        for (CargoItem cargoItem : cargoItems) {
            rectangles.add(new Rectangle(cargoItem.getWidth(), cargoItem.getLength()));
        }

        Input input = new Input(rectangles, cargoVanType.getWidth(), "CargoItems");

        LOGGER.debug("Запускаю алгоритм распределения");
        Output output = algorithm.solve(input);
        LOGGER.debug("Получаю результаты распределения");
        List<RectanglePlacement> resultPlacements = output.getPlacements();

        CargoVan van = new CargoVan(cargoVanType.getLength(), cargoVanType.getWidth());
        List<CargoItem> processedCargoItems = new ArrayList<>();
        LOGGER.debug("Обработка результатов распределения");
        for (int i = 0; i < cargoItems.size(); i++) {
            LOGGER.debug(
                    "Пытаюсь установить посылку в фургон на координаты: ({}, {}) посылка\n{}",
                    resultPlacements.get(i).getY(),
                    resultPlacements.get(i).getX(),
                    cargoItems.get(i).getName()
            );
            if (van.tryPuttingCargoItemAtCoordinates(cargoItems.get(i), resultPlacements.get(i).getY(), resultPlacements.get(i).getX())) {
                LOGGER.debug("Посылка успешно установлена в фургон");
                processedCargoItems.add(cargoItems.get(i));
            } else {
                LOGGER.debug("Не удалось установить посылку в фургон");
            }
        }

        for (CargoItem cargoItem : processedCargoItems) {
            LOGGER.debug("Удаляю посылку из списка незагруженных посылок\n{}", cargoItem.getName());
            cargoItems.remove(cargoItem);
        }
        LOGGER.debug("Формирование фургона завершено");
        return van;
    }
}
