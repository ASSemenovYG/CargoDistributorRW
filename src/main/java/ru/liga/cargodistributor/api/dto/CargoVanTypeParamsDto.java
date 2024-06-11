package ru.liga.cargodistributor.api.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class CargoVanTypeParamsDto {

    @Nullable
    private UUID id;

    @Nullable
    private String name;
}
