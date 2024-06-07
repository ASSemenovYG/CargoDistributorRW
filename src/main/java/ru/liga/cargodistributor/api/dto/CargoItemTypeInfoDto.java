package ru.liga.cargodistributor.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class CargoItemTypeInfoDto {
    private UUID id;
    private String name;
    private String legend;
    private String shape;
}
