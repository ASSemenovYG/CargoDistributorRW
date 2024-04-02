package ru.liga.dcs.algorithm;

import ru.liga.dcs.cargo.CargoList;
import ru.liga.dcs.cargo.CargoVan;

import java.util.List;

public abstract class DistributionAlgorithm {
    private final String name;
    private final List<CargoVan> loadedVans;

    public DistributionAlgorithm(String name, CargoList cargoList) {
        this.name = name;
        this.loadedVans = distributeCargo(cargoList);
    }

    public List<CargoVan> getLoadedVans() {
        return loadedVans;
    }

    public void printLoadedVans() {
        for (CargoVan cargoVan : loadedVans) {
            cargoVan.printVanLines();
            System.out.println("\r");
        }
    }

    public abstract List<CargoVan> distributeCargo(CargoList cargoList);
}