package ru.liga.cargodistributor.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@Builder
public class CargoItemTypeInfoCreateDto {
    private String name;
    private String legend;
    private MultipartFile multipartFile;
}
