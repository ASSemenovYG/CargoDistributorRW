package ru.liga.cargodistributor.cargo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cargo_item_type", schema = "cargo_distributor")
public class CargoItemTypeInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String legend;
    private String shape;


    public CargoItemTypeInfo(String name, String legend, String shape) {
        this.name = name;
        this.legend = legend;
        this.shape = shape;
    }

    public List<String> getShapeAsList() {
        return Arrays.asList(shape.split("\n"));
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Название посылки: ")
                .append(this.name)
                .append("\n")
                .append("Легенда: ")
                .append(this.legend)
                .append("\n")
                .append("Форма: ")
                .append("\n")
                .append("```")
                .append("\n")
                .append(this.shape)
                .append("\n")
                .append("```");
        return result.toString();
    }
}
