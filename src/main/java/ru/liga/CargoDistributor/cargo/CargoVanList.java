package ru.liga.CargoDistributor.cargo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.liga.CargoDistributor.algorithm.DistributionAlgorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Класс списка грузовых фургонов
 */
@JsonAutoDetect
@Component
public class CargoVanList {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    private final List<CargoVan> cargoVans;

    /**
     * Конструктор для десериализации
     */
    @Autowired
    public CargoVanList() {
        this.cargoVans = new ArrayList<>();
    }

    public CargoVanList(List<CargoVan> cargoVans) {
        this.cargoVans = cargoVans;
    }

    public List<CargoVan> getCargoVans() {
        return cargoVans;
    }

    public boolean isListSizeLessOrEqualThanMaxSize(int maxSize) {
        return cargoVans.size() <= maxSize;
    }

    /**
     * Распределяет посылки из списка по фургонам
     *
     * @param algorithm {@link DistributionAlgorithm} алгоритм распределения
     * @param cargoList {@link CargoItemList} список посылок
     */
    public void distributeCargo(DistributionAlgorithm algorithm, CargoItemList cargoList) {
        cargoVans.clear();
        cargoVans.addAll(algorithm.distributeCargo(cargoList));
        fillCoordinatesForLoadedCargoItems();
    }

    /**
     * @return String со всеми посылками из всех загруженных фургонов
     */
    public String getAllCargoItemNamesAsString() {
        StringBuilder sb = new StringBuilder();
        for (String cargoItemName : getAllCargoItemNames()) {
            sb.append("\n").append(cargoItemName).append("\n");
        }
        return sb.toString();
    }

    /**
     * @return String со всеми загруженными машинами для вывода в консоль
     */
    public String getCargoVanListAsString() {
        setAllCargoByCoordinates();
        StringBuilder sb = new StringBuilder();
        for (CargoVan cargoVan : cargoVans) {
            sb.append("\n").append(cargoVan.getVanCargoAsString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * @return Лист с посылками из всех фургонов в списке
     */
    @JsonIgnore
    public List<CargoItem> getAllCargoItemsFromVans() {
        return cargoVans.stream()
                .map(CargoVan::getLoadedCargoItems)
                .flatMap(Collection::stream)
                .toList();
    }

    private void setAllCargoByCoordinates() {
        for (CargoVan cargoVan : cargoVans) {
            cargoVan.initializeCargo();
            for (CargoItem cargoItem : cargoVan.getLoadedCargoItems()) {
                for (CargoItem.Coordinates coordinates : cargoItem.getCoordinates()) {
                    cargoVan.fillExactCargoVanCellByCoordinate(cargoItem, coordinates);
                }
            }
        }
    }

    private void fillCoordinatesForLoadedCargoItems() {
        for (CargoVan cargoVan : cargoVans) {
            for (CargoItem cargoItem : cargoVan.getLoadedCargoItems()) {
                cargoItem.fillCoordinatesByCargoVan(cargoVan);
            }
        }
    }

    private List<String> getAllCargoItemNames() {
        return cargoVans.stream()
                .map(CargoVan::getLoadedCargoItems)
                .flatMap(Collection::stream)
                .map(CargoItem::getName)
                .toList();
    }
}
