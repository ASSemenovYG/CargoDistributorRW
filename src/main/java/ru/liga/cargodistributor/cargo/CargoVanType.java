package ru.liga.cargodistributor.cargo;

import lombok.Getter;
import ru.liga.cargodistributor.cargo.entity.CargoVanTypeInfo;

@Getter
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

    public CargoVanType(CargoVanTypeInfo cargoVanTypeInfo) {
        super(cargoVanTypeInfo.getLength(), cargoVanTypeInfo.getWidth());
        this.name = cargoVanTypeInfo.getName();
        this.length = cargoVanTypeInfo.getLength();
        this.width = cargoVanTypeInfo.getWidth();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Название фургона: ")
                .append(this.name)
                .append("\n")
                .append("Ширина: ")
                .append(this.width)
                .append("\n")
                .append("Длина: ")
                .append(this.length)
                .append("\n");
        return result.toString();
    }
}
