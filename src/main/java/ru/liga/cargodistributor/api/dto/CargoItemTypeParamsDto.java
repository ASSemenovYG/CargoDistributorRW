package ru.liga.cargodistributor.api.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class CargoItemTypeParamsDto {

    @Nullable
    private UUID id;

    @Nullable
    private String name;

    @Nullable
    @Size(min = 1, message = "Legend should be one character long")
    @Size(max = 1, message = "Legend should be one character long")
    private String overrideLegend;

    @NotNull
    @Positive
    private int count;
}
