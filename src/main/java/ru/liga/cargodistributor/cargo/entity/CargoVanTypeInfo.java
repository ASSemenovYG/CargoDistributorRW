package ru.liga.cargodistributor.cargo.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "cargo_van_type", schema = "cargo_distributor")
public class CargoVanTypeInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private int width;
    private int height;

    public CargoVanTypeInfo(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public CargoVanTypeInfo(String name) {
        this(name, 0, 0);
    }

    public CargoVanTypeInfo() {
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Название фургона: ")
                .append(this.name)
                .append("\n")
                .append("Ширина: ")
                .append(this.width)
                .append("\n")
                .append("Высота: ");
        return result.toString();
    }
}
