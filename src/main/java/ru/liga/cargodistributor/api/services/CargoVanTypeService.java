package ru.liga.cargodistributor.api.services;

import ru.liga.cargodistributor.api.dto.CargoVanTypeInfoDto;

import java.util.Set;

public interface CargoVanTypeService {

    Set<CargoVanTypeInfoDto> getAllCargoVanTypes();

    CargoVanTypeInfoDto getCargoVanTypeByParams(String id, String name);

    CargoVanTypeInfoDto deleteCargoVanTypeById(String id);

    CargoVanTypeInfoDto createCargoVanTypeInfo(CargoVanTypeInfoDto source);

    CargoVanTypeInfoDto updateCargoVanTypeInfo(String id, CargoVanTypeInfoDto source);
}
