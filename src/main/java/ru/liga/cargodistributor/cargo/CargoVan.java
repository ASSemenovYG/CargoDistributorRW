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
    private static final String VAN_BORDER_SYMBOL = "+";
    private static final String EMPTY_CARGO_CELL_SYMBOL = " ";
    private static final String VAN_BACK_WALL = VAN_BORDER_SYMBOL.repeat(VAN_WIDTH + 2);

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

        public void setCargoItem(CargoItem cargoItem) {
            this.occupiedBy = cargoItem;
            this.cellItemTitle = String.valueOf(cargoItem.getSize());
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
        if (x + cargoItem.getLength() > CargoVan.VAN_LENGTH) {
            return false;
        }
        if (y + cargoItem.getWidth() > CargoVan.VAN_WIDTH) {
            return false;
        }
        for (int i = x; i < x + cargoItem.getLength(); i++) {
            for (int j = y; j < y + cargoItem.getWidth(); j++) {
                cargo[i][j].setCargoItem(cargoItem);
            }
        }
        loadedCargoItems.add(cargoItem);
        return true;
    }

    /**
     * Заполняет одну клетку в массиве cargo на указанных координатах
     *
     * @param cargoItem   Посылка
     * @param coordinates Координата XY
     */
    void fillExactCargoVanCellByCoordinate(CargoItem cargoItem, CargoItem.Coordinates coordinates) {
        cargo[coordinates.getX()][coordinates.getY()].setCargoItem(cargoItem);
    }

    /**
     * @return
     * String с кузовом грузовика в формате:
     *
     * <br>+8888  +
     * <br>+8888  +
     * <br>+118888+
     * <br>+224444+
     * <br>+224444+
     * <br>+333333+
     * <br>++++++++
     */
    public String getVanCargoAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = VAN_LENGTH - 1; i >= 0; i--) {
            if (i < VAN_LENGTH - 1) {
                sb.append("\n");
            }
            sb.append(VAN_BORDER_SYMBOL);
            for (int j = 0; j < VAN_WIDTH; j++) {
                sb.append((cargo[i][j].isEmpty()) ? EMPTY_CARGO_CELL_SYMBOL : cargo[i][j].getCellItemTitle());
            }
            sb.append(VAN_BORDER_SYMBOL);
        }
        sb.append("\n").append(VAN_BACK_WALL);
        LOGGER.trace("Returning to print cargo van:\n" + sb);
        return sb.toString();
    }

    public List<CargoItem> getLoadedCargoItems() {
        return loadedCargoItems;
    }

    private void fillSingleCargoItem(CargoItem cargoItem) {
        for (int i = 0; i < cargoItem.getLength(); i++) {
            for (int j = 0; j < cargoItem.getWidth(); j++) {
                cargo[i][j].setCargoItem(cargoItem);
            }
        }
        loadedCargoItems.add(cargoItem);
    }

    void initializeCargo() {
        for (int i = 0; i < VAN_LENGTH; i++) {
            for (int j = 0; j < VAN_WIDTH; j++) {
                cargo[i][j] = new CargoVanCell();
            }
        }
    }
}
