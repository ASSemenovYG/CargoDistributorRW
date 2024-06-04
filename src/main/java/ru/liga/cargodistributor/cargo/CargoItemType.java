package ru.liga.cargodistributor.cargo;

import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class CargoItemType extends CargoItem {
    private final String name;
    private final String legend;

    public CargoItemType(CargoItemTypeInfo cargoItemTypeInfo, CargoVan cargoVan) {
        super(new LinkedList<>(cargoItemTypeInfo.getShapeAsList()));
        this.name = cargoItemTypeInfo.getName();
        this.legend = cargoItemTypeInfo.getLegend();
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
        List<Coordinates> otherCoordinates = other.getCoordinates();
        if (this.getCoordinates().size() != otherCoordinates.size()) {
            return false;
        }
        return new HashSet<>(this.getCoordinates()).containsAll(otherCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, this.getWidth(), this.getLength(), this.getSize(), this.getCoordinates());
    }
}
