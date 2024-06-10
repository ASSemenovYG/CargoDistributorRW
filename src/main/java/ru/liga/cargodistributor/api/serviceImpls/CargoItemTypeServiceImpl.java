package ru.liga.cargodistributor.api.serviceImpls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public CargoItemTypeInfoDto getCargoItemTypeByParams(String id, String name) {
        CargoItemTypeInfo cargoItemTypeInfo;
        if (id != null && !id.isEmpty() && !id.isBlank()) {
            cargoItemTypeInfo = findCargoItemTypeById(id, StatusCode.CARGODISTR_002);
        } else if (name != null && !name.isEmpty() && !name.isBlank()) {
            cargoItemTypeInfo = findCargoItemTypeByName(name);
        } else {
            throw new ApiException("At least one of query parameters is required: id or name", StatusCode.CARGODISTR_003);
        }
        return cargoItemTypeMapper.fromEntityToDto(cargoItemTypeInfo);
    }

    @Override
    @Transactional
    public CargoItemTypeInfoDto deleteCargoItemTypeById(String id) {
        CargoItemTypeInfo cargoItemTypeInfo = findCargoItemTypeById(id, StatusCode.CARGODISTR_404);
        cargoItemTypeRepository.delete(cargoItemTypeInfo);
        return cargoItemTypeMapper.fromEntityToDto(cargoItemTypeInfo);
    }

    private CargoItemTypeInfo findCargoItemTypeById(String id, StatusCode exceptionStatusCode) {
        return cargoItemTypeRepository
                .findById(UUID.fromString(id))
                .orElseThrow(() -> new ApiException("Cargo Type wasn't found by id", exceptionStatusCode));
    }

    private CargoItemTypeInfo findCargoItemTypeByName(String name) {
        return cargoItemTypeRepository
                .findByName(name)
                .orElseThrow(() -> new ApiException("Cargo Type wasn't found by name", StatusCode.CARGODISTR_002));
    }
}
