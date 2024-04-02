package ru.liga.dcs.cargo;

import java.util.List;

/**
 * Интерфейс для получения списка посылок для последующего распределения
 */
public interface CargoList {
    /**
     * @return Список посылок
     */
    List<CargoItem> getCargo();

    /**
     * @return Лист с названиями всех посылок
     */
    List<String> getCargoItemNames();

    /**
     * @return True, если список посылок пустой или null
     */
    boolean isEmptyOrNull();
}
