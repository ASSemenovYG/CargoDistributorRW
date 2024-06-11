package ru.liga.cargodistributor.cargo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cargo_van_type", schema = "cargo_distributor")
public class CargoVanTypeInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private int width;
    private int length;

    public CargoVanTypeInfo(String name, int width, int length) {
        this.name = name;
        this.width = width;
        this.length = length;
    }

    public CargoVanTypeInfo(String name) {
        this(name, 0, 0);
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
                .append("Длина: ")
                .append(this.length)
                .append("\n");
        return result.toString();
    }
}
