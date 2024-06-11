package ru.liga.cargodistributor.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.liga.cargodistributor.algorithm.enums.DistributionAlgorithmName;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class DistributeByParamsDto {

    @NotNull
    private DistributionAlgorithmName algorithmName;

    @NotNull
    @Positive
    private int vanLimit;

    @NotNull
    private CargoVanTypeParamsDto cargoVanType;

    @NotNull
    @Valid
    private List<CargoItemTypeParamsDto> cargoItemTypes;
}
