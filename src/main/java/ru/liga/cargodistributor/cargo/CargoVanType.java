package ru.liga.cargodistributor.cargo;

public class CargoVanType extends CargoVan {
    private final String name;
    private final int length;
    private final int width;

    public CargoVanType(String name, int length, int width) {
        super(length, width);
        this.name = name;
        this.length = length;
        this.width = width;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public int getWidth() {
        return width;
    }
}
