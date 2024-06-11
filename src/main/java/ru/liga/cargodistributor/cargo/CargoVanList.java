package ru.liga.cargodistributor.cargo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.liga.cargodistributor.algorithm.CargoDistributionParameters;
import ru.liga.cargodistributor.algorithm.services.DistributionAlgorithmService;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Класс списка грузовых фургонов
 */
@Getter
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

    public boolean isListSizeLessOrEqualThanMaxSize(int maxSize) {
        return cargoVans.size() <= maxSize;
    }

    /**
     * Распределяет посылки из списка по фургонам
     *
     * @param algorithm {@link DistributionAlgorithmService} алгоритм распределения
     * @param cargoList {@link CargoItemList} список посылок
     */
    public void distributeCargo(DistributionAlgorithmService algorithm, CargoItemList cargoList) {
        cargoVans.clear();
        cargoVans.addAll(algorithm.distributeCargo(cargoList));
        fillCoordinatesForLoadedCargoItems();
    }

    /**
     * Распределяет посылки из списка по фургонам
     *
     * @param cargoDistributionParameters {@link CargoDistributionParameters} название алгоритма, параметры распределения (список типов посылок и тип фургона)
     */
    public void distributeCargoByParameters(CargoDistributionParameters cargoDistributionParameters) {
        cargoVans.clear();
        DistributionAlgorithmService algorithmService = DistributionAlgorithmService.createServiceByName(cargoDistributionParameters.getAlgorithmName());
        cargoVans.addAll(algorithmService.distributeCargoByParameters(cargoDistributionParameters));
        fillCoordinatesForLoadedCargoItems();
    }

    /**
     * @return String со всеми посылками из всех загруженных фургонов
     */
    @JsonIgnore
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
    @JsonIgnore
    public String getCargoVanListAsString(CargoConverterService cargoConverterService) {
        setAllCargoByCoordinates();
        return cargoConverterService.convertCargoVanListToString(this);
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
