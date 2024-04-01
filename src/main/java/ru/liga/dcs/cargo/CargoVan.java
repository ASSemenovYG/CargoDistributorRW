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
    private static final String VAN_BACK_WALL = VAN_BORDER_SYMBOL.repeat(VAN_WIDTH + 2);

    private List<CargoVanLine> lines;

    public CargoVan() {
        this.lines = new ArrayList<>(VAN_LENGTH);
    }

    public CargoVan(List<CargoVanLine> lines) {
        if (lines == null || lines.isEmpty()) {
            new CargoVan();
            return;
        }
        if (lines.size() > VAN_LENGTH) {
            throw new IllegalArgumentException("Cargo van length cannot be greater than " + VAN_WIDTH + "; Provided size of van lines: " + lines.size());
        }
        this.lines = new ArrayList<>(lines);
    }

    public void addLine(CargoVanLine line) {
        this.lines.add(line);
    }

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
            System.out.println(VAN_BORDER_SYMBOL + EMPTY_CARGO_CELL_SYMBOL.repeat(VAN_WIDTH) + VAN_BORDER_SYMBOL);
        }
    }

    public List<CargoVanLine> getLines() {
        return lines;
    }

    /**
     * Класс погрузочной линии (паллета) в кузове грузовой машины
     */
    public static class CargoVanLine {
        private List<CargoVanCell> line;

        public CargoVanLine() {
            this.line = new ArrayList<>(VAN_WIDTH);
        }

        public CargoVanLine(List<CargoVanCell> cells) {
            if (cells == null || cells.isEmpty()) {
                new CargoVanLine();
                return;
            }
            if (cells.size() > VAN_WIDTH) {
                throw new IllegalArgumentException("Cargo van width cannot be greater than " + VAN_WIDTH + "; Provided size of line: " + cells.size());
            }
            this.line = new ArrayList<>(cells);
        }

        public CargoVanLine(CargoItem cargoItem) {
            this.line = new ArrayList<>(VAN_WIDTH);
            addCargoItem(cargoItem, 0);
        }

        void addCargoItem(CargoItem cargoItem, int index) {
            for (int i = 0; i < cargoItem.getLength(); i++) {
                this.line.add(index, new CargoVanCell(cargoItem.getName().substring(i, i + 1)));
                index++;
            }
        }

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


}
