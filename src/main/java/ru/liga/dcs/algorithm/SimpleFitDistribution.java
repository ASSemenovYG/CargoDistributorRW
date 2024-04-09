package ru.liga.dcs.algorithm;

import com.googlecode.caparf.algorithms.spp2d.SimpleFit;
import com.googlecode.caparf.framework.base.Algorithm;
import com.googlecode.caparf.framework.items.Rectangle;
import com.googlecode.caparf.framework.items.RectanglePlacement;
import com.googlecode.caparf.framework.spp2d.Input;
import com.googlecode.caparf.framework.spp2d.Output;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.liga.dcs.cargo.CargoItem;
import ru.liga.dcs.cargo.CargoList;
import ru.liga.dcs.cargo.CargoVan;

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
public class SimpleFitDistribution extends DistributionAlgorithm {
    private static final Logger LOGGER = LogManager.getLogger(SimpleFitDistribution.class);

    /**
     * @param cargoList Список посылок для распределения
     */
    public SimpleFitDistribution(CargoList cargoList) {
        super(cargoList);
    }

    @Override
    public List<CargoVan> distributeCargo(CargoList cargoList) {
        List<CargoVan> result = new ArrayList<>();
        List<CargoItem> processingCargoList = new ArrayList<>(cargoList.getCargo());

        while (!processingCargoList.isEmpty()) {
            LOGGER.debug("Запускаю формирование следующего фургона");
            result.add(getNextFilledVan(processingCargoList));
        }
        LOGGER.info("Распределение посылок завершено. Итоговое количество фургонов: " + result.size());
        return result;
    }

    private CargoVan getNextFilledVan(List<CargoItem> cargoItems) {
        Algorithm<Input, Output> algorithm = new SimpleFit(SimpleFit.ItemOrder.FIRST_FIT, SimpleFit.PlacementStrategy.DEFAULT);
        List<Rectangle> rectangles = new ArrayList<>();

        for (CargoItem cargoItem : cargoItems) {
            rectangles.add(new Rectangle(cargoItem.getWidth(), cargoItem.getLength()));
        }

        Input input = new Input(rectangles, CargoVan.VAN_WIDTH, "CargoItems");

        LOGGER.debug("Запускаю алгоритм распределения");
        Output output = algorithm.solve(input);
        LOGGER.debug("Получаю результаты распределения");
        List<RectanglePlacement> resultPlacements = output.getPlacements();

        CargoVan van = new CargoVan();
        List<CargoItem> processedCargoItems = new ArrayList<>();
        LOGGER.debug("Обработка результатов распределения");
        for (int i = 0; i < cargoItems.size(); i++) {
            LOGGER.debug(
                    "Пытаюсь установить посылку в фургон на координаты: (" +
                            resultPlacements.get(i).getY() +
                            ", " +
                            resultPlacements.get(i).getX() +
                            ") посылка\n" +
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
            LOGGER.debug("Удаляю посылку из списка незагруженных посылок\n" + cargoItem.getName());
            cargoItems.remove(cargoItem);
        }
        LOGGER.debug("Формирование фургона завершено");
        return van;
    }
}
