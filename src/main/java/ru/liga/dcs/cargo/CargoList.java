package ru.liga.dcs.cargo;

import java.util.List;

public interface CargoList {
    List<CargoItem> getCargo();

    List<String> getCargoItemNames();

    boolean isEmptyOrNull();
}
