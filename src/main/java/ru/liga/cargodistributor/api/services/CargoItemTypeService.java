package ru.liga.cargodistributor.api.services;

import ru.liga.cargodistributor.api.dto.CargoItemTypeInfoDto;

import java.util.Set;

public interface CargoItemTypeService {

    Set<CargoItemTypeInfoDto> getAllCargoItemTypes();
}
