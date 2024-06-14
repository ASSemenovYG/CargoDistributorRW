package ru.liga.cargodistributor.api.services;

import ru.liga.cargodistributor.api.dto.CargoItemTypeInfoCreateDto;
import ru.liga.cargodistributor.api.dto.CargoItemTypeInfoDto;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;

import java.util.Set;

public interface CargoItemTypeService {

    Set<CargoItemTypeInfoDto> getAllCargoItemTypes();

    CargoItemTypeInfoDto getCargoItemTypeByParams(String id, String name);

    CargoItemTypeInfoDto deleteCargoItemTypeById(String id);

    CargoItemTypeInfoDto createCargoItemTypeInfo(CargoItemTypeInfoCreateDto source);

    CargoItemTypeInfoDto updateCargoItemTypeInfo(String id, CargoItemTypeInfoCreateDto source);

    CargoItemTypeInfo findCargoItemTypeInfoByParams(String id, String name);
}
