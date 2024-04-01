package ru.liga.dcs.cargo;

public class CargoVanCell {
    private static final int MAX_LENGTH_CELL = 1;
    private String cellItemTitle;

    public CargoVanCell(String cellItemTitle) {
        if (cellItemTitle != null && cellItemTitle.length() > MAX_LENGTH_CELL) {
            throw new IllegalArgumentException("Cell item length cannot be greater than: " + MAX_LENGTH_CELL + " ; Provided cell item: " + cellItemTitle);
        }
        this.cellItemTitle = cellItemTitle;
    }

    public String getCellItemTitle() {
        return cellItemTitle;
    }

    public void clearCellItemTitle() {
        this.cellItemTitle = null;
    }

    public boolean isNullOrEmpty() {
        if (this.cellItemTitle == null) {
            return true;
        }
        return cellItemTitle.isEmpty();
    }
}
