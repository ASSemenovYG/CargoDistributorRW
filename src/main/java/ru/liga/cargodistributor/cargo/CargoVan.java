package ru.liga.cargodistributor.cargo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Класс кузова грузовой машины
 */
@JsonAutoDetect
public class CargoVan {
    private static final Logger LOGGER = LoggerFactory.getLogger(CargoVan.class);
    public static final int VAN_LENGTH = 6;
    public static final int VAN_WIDTH = 6;

    /**
     * Класс погрузочной единицы (клетки) в кузове грузовой машины
     */
    public static class CargoVanCell {
        private String cellItemTitle;
        private CargoItem occupiedBy;

        public CargoVanCell() {
            this((CargoItem) null);
        }

        public CargoVanCell(CargoItem cargoItem) {
            this.occupiedBy = cargoItem;
            this.cellItemTitle = (cargoItem == null) ? "" : String.valueOf(cargoItem.getSize());
        }

        public CargoItem getOccupiedBy() {
            return occupiedBy;
        }

        public boolean isEmpty() {
            return occupiedBy == null;
        }

        public String getCellItemTitle() {
            return cellItemTitle;
        }

        @Override
        public boolean equals(Object o) {
            if (getClass() != o.getClass()) {
                return false;
            }
            CargoVanCell other = (CargoVanCell) o;
            return Objects.equals(this.cellItemTitle, other.getCellItemTitle()) && Objects.equals(getOccupiedBy(), other.getOccupiedBy());
        }

        private void setCargoItem(CargoItem cargoItem) {
            this.occupiedBy = cargoItem;
            this.cellItemTitle = String.valueOf(cargoItem.getSize());
        }
    }

    @JsonIgnore
    private final CargoVanCell[][] cargo = new CargoVanCell[VAN_LENGTH][VAN_WIDTH];
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    private final List<CargoItem> loadedCargoItems;

    /**
     * Создает грузовой фургон с пустым кузовом
     */
    public CargoVan() {
        initializeCargo();
        this.loadedCargoItems = new ArrayList<>();
    }

    /**
     * Создает грузовой фургон с одной посылкой внутри
     *
     * @param cargoItem Посылка
     */
    public CargoVan(CargoItem cargoItem) {
        this();
        fillSingleCargoItem(cargoItem);
    }

    public CargoVanCell[][] getCargo() {
        return this.cargo;
    }

    /**
     * Добавляет посылку в грузовой фургон, начиная с координат X и Y, соответствующих левой нижней клетке посылки
     *
     * @param cargoItem Посылка
     * @param x         Координата по длине
     * @param y         Координата по ширине
     * @return true, если возможно добавить посылку с указанными координатами, иначе false
     */
    public boolean tryPuttingCargoItemAtCoordinates(CargoItem cargoItem, int x, int y) {
        if (x + cargoItem.getLength() > VAN_LENGTH) {
            LOGGER.debug(
                    "tryPuttingCargoItemAtCoordinates: Невозможно добавить в фургон посылку {} с начальными координатами [{}],[{}], превышено ограничение по длине фургона",
                    cargoItem.getName(),
                    x,
                    y
            );
            return false;
        }
        if (y + cargoItem.getWidth() > VAN_WIDTH) {
            LOGGER.debug(
                    "tryPuttingCargoItemAtCoordinates: Невозможно добавить в фургон посылку {} с начальными координатами [{}],[{}], превышено ограничение по ширине фургона",
                    cargoItem.getName(),
                    x,
                    y
            );
            return false;
        }
        for (int i = x; i < x + cargoItem.getLength(); i++) {
            for (int j = y; j < y + cargoItem.getWidth(); j++) {
                LOGGER.debug("tryPuttingCargoItemAtCoordinates: Заполняю координату [{}],[{}] посылкой {}", i, j, cargoItem.getName());
                cargo[i][j].setCargoItem(cargoItem);
            }
        }
        loadedCargoItems.add(cargoItem);
        LOGGER.info("Посылка {} успешно добавлена в фургон", cargoItem.getName());
        return true;
    }

    public List<CargoItem> getLoadedCargoItems() {
        return loadedCargoItems;
    }

    /**
     * Заполняет одну клетку в массиве cargo на указанных координатах
     *
     * @param cargoItem   Посылка
     * @param coordinates Координата XY
     */
    void fillExactCargoVanCellByCoordinate(CargoItem cargoItem, CargoItem.Coordinates coordinates) {
        LOGGER.debug("fillExactCargoVanCellByCoordinate: Заполняю координату [{}],[{}] посылкой {}", coordinates.getX(), coordinates.getY(), cargoItem.getName());
        cargo[coordinates.getX()][coordinates.getY()].setCargoItem(cargoItem);
    }

    /**
     * Заполняет двумерный массив кузова фургона пустыми клетками
     */
    void initializeCargo() {
        for (int i = 0; i < VAN_LENGTH; i++) {
            for (int j = 0; j < VAN_WIDTH; j++) {
                cargo[i][j] = new CargoVanCell();
            }
        }
    }

    private void fillSingleCargoItem(CargoItem cargoItem) {
        for (int i = 0; i < cargoItem.getLength(); i++) {
            for (int j = 0; j < cargoItem.getWidth(); j++) {
                cargo[i][j].setCargoItem(cargoItem);
            }
        }
        loadedCargoItems.add(cargoItem);
    }
}
