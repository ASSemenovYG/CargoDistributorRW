package ru.liga.cargodistributor.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.liga.cargodistributor.api.dto.CargoItemTypeInfoCreateDto;
import ru.liga.cargodistributor.api.dto.CargoItemTypeInfoDto;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CargoItemTypeMapper {

    List<CargoItemTypeInfoDto> toDtoList(List<CargoItemTypeInfo> source);

    CargoItemTypeInfoDto fromEntityToDto(CargoItemTypeInfo source);

    CargoItemTypeInfo createEntityFromDto (CargoItemTypeInfoCreateDto source);
}
