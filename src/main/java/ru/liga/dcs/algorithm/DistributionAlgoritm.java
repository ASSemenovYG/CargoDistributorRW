package ru.liga.dcs.algorithm;

import ru.liga.dcs.cargo.CargoList;
import ru.liga.dcs.cargo.CargoVan;

import java.util.List;

abstract class DistributionAlgorithm {
    private final String name;
    private final List<CargoVan> loadedVans;

    public DistributionAlgorithm(String name, CargoList cargoList) {
        this.name = name;
        this.loadedVans = distributeCargo(cargoList);
    }

    public String getName() {
        return name;
    }

    public void printLoadedVans() {
        for (CargoVan cargoVan : loadedVans) {
            cargoVan.printVanLines();
            System.out.println("\r");
        }
    }

    public abstract List<CargoVan> distributeCargo(CargoList cargoList);
}