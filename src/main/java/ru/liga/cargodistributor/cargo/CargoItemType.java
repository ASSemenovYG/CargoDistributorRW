package ru.liga.cargodistributor.cargo;

import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;

import java.util.LinkedList;

public class CargoItemType extends CargoItem {
    private final String name;
    private String legend;

    public CargoItemType(CargoItemTypeInfo cargoItemTypeInfo, CargoVan cargoVan) {
        super(new LinkedList<>(cargoItemTypeInfo.getShapeAsList()));
        this.name = cargoItemTypeInfo.getName();
        this.legend = cargoItemTypeInfo.getLegend();
    }

}
