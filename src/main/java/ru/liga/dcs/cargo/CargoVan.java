package ru.liga.dcs.cargo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Класс кузова грузовой машины
 */
@JsonAutoDetect
public class CargoVan {
    private static final Logger LOGGER = LogManager.getLogger(CargoVan.class);
    public static final int VAN_LENGTH = 6;
    public static final int VAN_WIDTH = 6;
    private static final String VAN_BORDER_SYMBOL = "+";
    private static final String EMPTY_CARGO_CELL_SYMBOL = " ";
    @Deprecated
    private static final String VAN_EMPTY_LINE = VAN_BORDER_SYMBOL + (EMPTY_CARGO_CELL_SYMBOL.repeat(VAN_WIDTH)) + VAN_BORDER_SYMBOL;
    private static final String VAN_BACK_WALL = VAN_BORDER_SYMBOL.repeat(VAN_WIDTH + 2);

    /**
     * Класс погрузочной единицы (клетки) в кузове грузовой машины
     */
    public static class CargoVanCell {
        @Deprecated
        private static final int MAX_LENGTH_CELL = 1;
        private String cellItemTitle;
        private CargoItem occupiedBy;

        public CargoVanCell() {
            this((CargoItem) null);
        }

        public CargoVanCell(CargoItem cargoItem) {
            this.occupiedBy = cargoItem;
            this.cellItemTitle = (cargoItem == null) ? "" : String.valueOf(cargoItem.getSize());
        }

        /**
         * @deprecated Более не используется в связи с тем, что теперь клетка хранит ссылку на посылку, которая занимает клетку
         */
        @Deprecated
        public CargoVanCell(String cellItemTitle) {
            if (cellItemTitle != null && cellItemTitle.length() > MAX_LENGTH_CELL) {
                throw new IllegalArgumentException("Cell item length cannot be greater than: " + MAX_LENGTH_CELL + " ; Provided cell item: " + cellItemTitle);
            }
            this.cellItemTitle = cellItemTitle;
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

        /**
         * @deprecated Вместо него использовать метод isEmpty
         */
        @Deprecated
        public boolean isNullOrEmpty() {
            if (this.cellItemTitle == null) {
                return true;
            }
            return cellItemTitle.isEmpty();
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

    /**
     * Класс погрузочной линии (паллета) в кузове грузовой машины
     *
     * @deprecated Теперь вместо листа погрузочных линий в {@link CargoVan} используется двумерный массив cargo
     */
    @Deprecated
    public static class CargoVanLine {
        private final List<CargoVanCell> line;

        public CargoVanLine() {
            this.line = new ArrayList<>(VAN_WIDTH);
        }

        public CargoVanLine(CargoItem cargoItem) {
            this.line = new ArrayList<>(VAN_WIDTH);
            addCargoItem(cargoItem, 0);
        }

        /**
         * Добавляет посылку целиком на паллет (погрузочную линию фургона), начиная с указанного индекса
         *
         * @param cargoItem Посылка
         * @param index     Индекс на погрузочной линии
         */
        public void addCargoItem(CargoItem cargoItem, int index) {
            for (int i = 0; i < cargoItem.getLength(); i++) {
                this.line.add(index, new CargoVanCell(cargoItem.getName().substring(i, i + 1)));
                index++;
            }
        }

        /**
         * Выводит паллет (погрузочную линию) в консоль в виде:
         * +666666+
         */
        void printLine() {
            StringBuilder sb = new StringBuilder();
            for (CargoVanCell cargoVanCell : line) {
                sb.append((cargoVanCell.isNullOrEmpty()) ? EMPTY_CARGO_CELL_SYMBOL : cargoVanCell.getCellItemTitle());
            }
            if (sb.length() < VAN_WIDTH) {
                sb.append(EMPTY_CARGO_CELL_SYMBOL.repeat(VAN_WIDTH - sb.length()));
            }
            sb.insert(0, VAN_BORDER_SYMBOL);
            sb.insert(sb.length(), VAN_BORDER_SYMBOL);
            System.out.println(sb);
        }
    }

    /**
     * @deprecated Более не используется в связи с отказом от {@link CargoVanLine}
     */
    @Deprecated
    private final List<CargoVanLine> lines = new ArrayList<>(0);

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
     * Выводит кузов грузовика в консоль в формате:
     *
     * <br>+8888  +
     * <br>+8888  +
     * <br>+118888+
     * <br>+224444+
     * <br>+224444+
     * <br>+333333+
     * <br>++++++++
     */
    public void printVanCargo() {
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
        LOGGER.trace("Printing cargo van:\n" + sb);
        System.out.println(sb);
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

    /**
     * Добавляет паллет (погрузочную линию) в грузовой фургон
     *
     * @param line Паллет (погрузочная линия)
     * @deprecated Более не используется в связи с отказом от {@link CargoVanLine}
     */
    @Deprecated
    public void addLine(CargoVanLine line) {
        this.lines.add(line);
    }

    /**
     * @deprecated Заменен на метод printVanCargo в связи с отказом от {@link CargoVanLine}
     */
    @Deprecated
    public void printVanLines() {
        if (lines.size() < VAN_LENGTH) {
            this.printEmptyLines(VAN_LENGTH - lines.size());
        }
        for (int i = lines.size() - 1; i >= 0; i--) {
            lines.get(i).printLine();
        }
        System.out.println(VAN_BACK_WALL);
    }

    /**
     * @deprecated Более не используется в связи с переходом на printVanCargo и отказе от {@link CargoVanLine}
     */
    @Deprecated
    private void printEmptyLines(int count) {
        for (int i = 0; i < count; i++) {
            System.out.println(VAN_EMPTY_LINE);
        }
    }
}
