package ru.liga.cargodistributor.cargo;

import java.util.LinkedList;
import java.util.UUID;

public class CargoItemType extends CargoItem {
    //todo: разобраться, нужен этот класс вообще или нет
    private final UUID id;
    private final String name;
    private String legend;

    public CargoItemType(UUID id, String name, String legend, int size, int length, int width) {
        super(size, length, width);
        this.id = id;
        this.name = name;
        this.legend = legend;
    }

    public CargoItemType(UUID id, String name, String legend, LinkedList<String> unparsedCargoItem) {
        super(unparsedCargoItem);
        this.id = id;
        this.name = name;
        this.legend = legend;
    }

}
