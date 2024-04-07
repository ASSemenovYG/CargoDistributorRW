package ru.liga.dcs.cargo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Оберточный класс для сериализации/десериализации списка грузовых фургонов
 */
@JsonAutoDetect
public class CargoVanList {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    private final List<CargoVan> cargoVans;

    /**
     * Конструктор для десериализации
     */
    public CargoVanList() {
        this.cargoVans = new ArrayList<>();
    }

    public CargoVanList(List<CargoVan> cargoVans) {
        this.cargoVans = cargoVans;
    }

    public List<CargoVan> getCargoVans() {
        return cargoVans;
    }
}
