package ru.liga.cargodistributor.api.serviceImpls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.liga.cargodistributor.api.dto.CargoItemTypeInfoDto;
import ru.liga.cargodistributor.api.enums.StatusCode;
import ru.liga.cargodistributor.api.exceptions.ApiException;
import ru.liga.cargodistributor.api.mapper.CargoItemTypeMapper;
import ru.liga.cargodistributor.api.services.CargoItemTypeService;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoItemTypeRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CargoItemTypeServiceImpl implements CargoItemTypeService {

    private final CargoItemTypeRepository cargoItemTypeRepository;
    private final CargoItemTypeMapper cargoItemTypeMapper;

    @Override
    public Set<CargoItemTypeInfoDto> getAllCargoItemTypes() {
        return new HashSet<>(cargoItemTypeMapper.toDtoList(cargoItemTypeRepository.findAll()));
    }

    @Override
    public CargoItemTypeInfoDto getCargoItemByParams(String id, String name) {
        CargoItemTypeInfo cargoItemTypeInfo;
        if (id != null && !id.isEmpty() && !id.isBlank()) {
            cargoItemTypeInfo = cargoItemTypeRepository
                    .findById(UUID.fromString(id))
                    .orElseThrow(() -> new ApiException("Cargo Type wasn't found by id", StatusCode.CARGODISTR_002));
        } else if (name != null && !name.isEmpty() && !name.isBlank()) {
            cargoItemTypeInfo = cargoItemTypeRepository
                    .findByName(name)
                    .orElseThrow(() -> new ApiException("Cargo Type wasn't found by name", StatusCode.CARGODISTR_002));
        } else {
            throw new ApiException("At least one of query parameters is required: id or name", StatusCode.CARGODISTR_003);
        }
        return cargoItemTypeMapper.fromEntityToDto(cargoItemTypeInfo);
    }
}
