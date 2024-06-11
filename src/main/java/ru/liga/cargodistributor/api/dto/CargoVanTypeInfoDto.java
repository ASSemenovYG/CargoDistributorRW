package ru.liga.cargodistributor.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class CargoVanTypeInfoDto {
    public interface New {
    }

    public interface Update {
    }

    private UUID id;

    @NotNull
    private String name;

    @NotNull(groups = {New.class})
    private int width;

    @NotNull(groups = {New.class})
    private int length;
}
