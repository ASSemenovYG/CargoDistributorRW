package ru.liga.dcs.cargo;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс кузова грузовой машины
 */
public class CargoVan {
    public static final int VAN_LENGTH = 6;
    public static final int VAN_WIDTH = 6;
    private static final String VAN_BORDER_SYMBOL = "+";
    private static final String EMPTY_CARGO_CELL_SYMBOL = " ";
    private static final String VAN_EMPTY_LINE = VAN_BORDER_SYMBOL + (EMPTY_CARGO_CELL_SYMBOL.repeat(VAN_WIDTH)) + VAN_BORDER_SYMBOL;
    private static final String VAN_BACK_WALL = VAN_BORDER_SYMBOL.repeat(VAN_WIDTH + 2);

    /**
     * Класс погрузочной линии (паллета) в кузове грузовой машины
     */
    public static class CargoVanLine {
        /**
         * Класс погрузочной единицы (клетки) на паллете в кузове грузовой машины
         */
        public static class CargoVanCell {
            private static final int MAX_LENGTH_CELL = 1;
            private final String cellItemTitle;

            public CargoVanCell(String cellItemTitle) {
                if (cellItemTitle != null && cellItemTitle.length() > MAX_LENGTH_CELL) {
                    throw new IllegalArgumentException("Cell item length cannot be greater than: " + MAX_LENGTH_CELL + " ; Provided cell item: " + cellItemTitle);
                }
                this.cellItemTitle = cellItemTitle;
            }

            public String getCellItemTitle() {
                return cellItemTitle;
            }

            public boolean isNullOrEmpty() {
                if (this.cellItemTitle == null) {
                    return true;
                }
                return cellItemTitle.isEmpty();
            }
        }

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

    private final List<CargoVanLine> lines;

    public CargoVan() {
        this.lines = new ArrayList<>(VAN_LENGTH);
    }

    /**
     * Добавляет паллет (погрузочную линию) в грузовой фургон
     *
     * @param line Паллет (погрузочная линия)
     */
    public void addLine(CargoVanLine line) {
        this.lines.add(line);
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
    public void printVanLines() {
        if (lines.size() < VAN_LENGTH) {
            this.printEmptyLines(VAN_LENGTH - lines.size());
        }
        for (int i = lines.size() - 1; i >= 0; i--) {
            lines.get(i).printLine();
        }
        System.out.println(VAN_BACK_WALL);
    }

    private void printEmptyLines(int count) {
        for (int i = 0; i < count; i++) {
            System.out.println(VAN_EMPTY_LINE);
        }
    }
}
