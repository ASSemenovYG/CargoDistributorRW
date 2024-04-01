package ru.liga.dcs.cargo;

import java.util.List;

public class CargoListMock implements CargoList {
    private final List<String> cargo;

    public CargoListMock(List<String> cargo) {
        this.cargo = cargo;
    }

    public List<String> getCargo() {
        return cargo;
    }

    public boolean isEmpty() {
        return cargo.isEmpty();
    }
}
