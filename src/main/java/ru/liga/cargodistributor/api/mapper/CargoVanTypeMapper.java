package ru.liga.cargodistributor.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.liga.cargodistributor.api.dto.CargoVanTypeInfoDto;
import ru.liga.cargodistributor.cargo.entity.CargoVanTypeInfo;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CargoVanTypeMapper {

    List<CargoVanTypeInfoDto> toDtoList(List<CargoVanTypeInfo> source);

    CargoVanTypeInfoDto fromEntityToDto(CargoVanTypeInfo source);

    CargoVanTypeInfo fromDtoToEntity(CargoVanTypeInfoDto source);
}
