package ru.liga.cargodistributor.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import ru.liga.cargodistributor.algorithm.enums.DistributionAlgorithmName;

@Data
@AllArgsConstructor
@Builder
public class DistributeByFileDto {
    @NotNull
    private DistributionAlgorithmName algorithmName;
    @NotNull
    private int vanLimit;
    @NotNull
    private MultipartFile cargoItemListFile;
}
