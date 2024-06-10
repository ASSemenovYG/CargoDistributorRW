package ru.liga.cargodistributor.cargo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@JsonAutoDetect
@JsonTypeName("CargoItemType")
public class CargoItemType extends CargoItem {
    private final String name;
    private String legend;

    /**
     * Конструктор для десериализации
     */
    public CargoItemType() {
        super();
        this.name = null;
        this.legend = null;
    }

    public CargoItemType(CargoItemTypeInfo cargoItemTypeInfo) {
        super(new LinkedList<>(cargoItemTypeInfo.getShapeAsList()));
        this.name = cargoItemTypeInfo.getName();
        this.legend = cargoItemTypeInfo.getLegend();
    }

    private CargoItemType(CargoItem cargoItem) {
        super(cargoItem);
        this.name = cargoItem.getName();
        this.legend = cargoItem.getLegend();
    }

    @Override
    public void setLegend(String legend) {
        this.legend = legend;
    }

    @Override
    public String getLegend() {
        return legend;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (getClass() != o.getClass()) {
            return false;
        }
        CargoItemType other = (CargoItemType) o;
        if (!Objects.equals(this.name, other.getName())) {
            return false;
        }
        if (this.getWidth() != other.getWidth()) {
            return false;
        }
        if (this.getLength() != other.getLength()) {
            return false;
        }
        if (this.getSize() != other.getSize()) {
            return false;
        }
        if (!Objects.equals(this.getLegend(), other.getLegend())) {
            return false;
        }
        List<Coordinates> otherCoordinates = other.getCoordinates();
        if (this.getCoordinates().size() != otherCoordinates.size()) {
            return false;
        }
        return new HashSet<>(this.getCoordinates()).containsAll(otherCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, this.getWidth(), this.getLength(), this.getSize(), this.getLegend(), this.getCoordinates());
    }

    @Override
    public CargoItem copy() {
        return new CargoItemType(this);
    }
}
