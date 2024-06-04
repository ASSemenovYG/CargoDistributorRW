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
    public static final int DEFAULT_VAN_LENGTH = 6;
    public static final int DEFAULT_VAN_WIDTH = 6;
    public static final String DEFAULT_VAN_NAME = "Грузовой фургон 6х6 по умолчанию";

    @JsonIgnore
    private final int length;
    @JsonIgnore
    private final int width;

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
            this.cellItemTitle = (cargoItem == null) ? "" : cargoItem.getLegend();
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
            this.cellItemTitle = cargoItem.getLegend();
        }
    }

    @JsonIgnore
    private final CargoVanCell[][] cargo;
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    private final List<CargoItem> loadedCargoItems;

    /**
     * Создает грузовой фургон с пустым кузовом с размерами по умолчанию
     */
    public CargoVan() {
        this(DEFAULT_VAN_LENGTH, DEFAULT_VAN_WIDTH);
    }

    /**
     * Создает грузовой фургон с пустым кузовом с заданными размерами
     *
     * @param length длина фургона
     * @param width  ширина фургона
     */
    public CargoVan(int length, int width) {
        if (length < 1) {
            throw new IllegalArgumentException("Длина фургона должна быть больше 0, переданная длина: " + length);
        }
        if (width < 1) {
            throw new IllegalArgumentException("Ширина фургона должна быть больше 0, переданная ширина: " + width);
        }
        this.length = length;
        this.width = width;
        this.cargo = new CargoVanCell[this.length][this.width];
        initializeCargo();
        this.loadedCargoItems = new ArrayList<>();
    }

    /**
     * Создает грузовой фургон с размерами по умолчанию с одной посылкой внутри
     *
     * @param cargoItem Посылка
     */
    public CargoVan(CargoItem cargoItem) {
        this();
        fillSingleCargoItem(cargoItem);
    }

    /**
     * Создает грузовой фургон с заданными размерами с одной посылкой внутри
     *
     * @param length    длина фургона
     * @param width     ширина фургона
     * @param cargoItem Посылка
     */
    public CargoVan(int length, int width, CargoItem cargoItem) {
        this(length, width);
        fillSingleCargoItem(cargoItem);
    }

    public CargoVanCell[][] getCargo() {
        return this.cargo;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Название фургона: ")
                .append(DEFAULT_VAN_NAME)
                .append("\n")
                .append("Ширина: ")
                .append(this.width)
                .append("\n")
                .append("Длина: ")
                .append(this.length)
                .append("\n");
        return result.toString();
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
        if (x + cargoItem.getLength() > this.length) {
            LOGGER.debug(
                    "tryPuttingCargoItemAtCoordinates: Невозможно добавить в фургон посылку {} с начальными координатами [{}],[{}], превышено ограничение по длине фургона",
                    cargoItem.getName(),
                    x,
                    y
            );
            return false;
        }
        if (y + cargoItem.getWidth() > this.width) {
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
        for (int i = 0; i < this.length; i++) {
            for (int j = 0; j < this.width; j++) {
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
