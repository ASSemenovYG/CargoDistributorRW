package ru.liga.cargodistributor.api.serviceImpls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.liga.cargodistributor.api.dto.CargoVanTypeInfoDto;
import ru.liga.cargodistributor.api.enums.StatusCode;
import ru.liga.cargodistributor.api.exceptions.ApiException;
import ru.liga.cargodistributor.api.mapper.CargoVanTypeMapper;
import ru.liga.cargodistributor.api.services.CargoVanTypeService;
import ru.liga.cargodistributor.cargo.CargoVanType;
import ru.liga.cargodistributor.cargo.entity.CargoVanTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoVanTypeRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CargoVanTypeServiceImpl implements CargoVanTypeService {

    private final CargoVanTypeRepository cargoVanTypeRepository;
    private final CargoVanTypeMapper cargoVanTypeMapper;

    @Override
    public Set<CargoVanTypeInfoDto> getAllCargoVanTypes() {
        return new HashSet<>(cargoVanTypeMapper.toDtoList(cargoVanTypeRepository.findAll()));
    }

    @Override
    public CargoVanTypeInfoDto getCargoVanTypeByParams(String id, String name) {
        CargoVanTypeInfo cargoVanTypeInfo;
        if (id != null && !id.isEmpty() && !id.isBlank()) {
            cargoVanTypeInfo = findCargoVanTypeById(id, StatusCode.CARGODISTR_005);
        } else if (name != null && !name.isEmpty() && !name.isBlank()) {
            cargoVanTypeInfo = findCargoVanTypeByName(name);
        } else {
            throw new ApiException("At least one of query parameters is required: id or name", StatusCode.CARGODISTR_003);
        }
        return cargoVanTypeMapper.fromEntityToDto(cargoVanTypeInfo);
    }

    @Override
    @Transactional
    public CargoVanTypeInfoDto deleteCargoVanTypeById(String id) {
        CargoVanTypeInfo cargoVanTypeInfo = findCargoVanTypeById(id, StatusCode.CARGODISTR_404);
        cargoVanTypeRepository.delete(cargoVanTypeInfo);
        return cargoVanTypeMapper.fromEntityToDto(cargoVanTypeInfo);
    }

    @Override
    @Transactional
    public CargoVanTypeInfoDto createCargoVanTypeInfo(CargoVanTypeInfoDto source) {
        try {
            CargoVanTypeInfo cargoVanTypeInfo = cargoVanTypeMapper.fromDtoToEntity(source);
            new CargoVanType(cargoVanTypeInfo);
            cargoVanTypeRepository.save(cargoVanTypeInfo);
            return cargoVanTypeMapper.fromEntityToDto(cargoVanTypeInfo);
        } catch (RuntimeException e) {
            throw new ApiException("Error occurred while creating cargo van type: " + e.getMessage(), StatusCode.CARGODISTR_500);
        }
    }

    @Override
    @Transactional
    public CargoVanTypeInfoDto updateCargoVanTypeInfo(String id, CargoVanTypeInfoDto source) {
        CargoVanTypeInfo cargoVanTypeInfo = findCargoVanTypeById(id, StatusCode.CARGODISTR_404);

        cargoVanTypeInfo.setName(source.getName());
        cargoVanTypeInfo.setWidth(source.getWidth());
        cargoVanTypeInfo.setLength(source.getLength());
        try {
            new CargoVanType(cargoVanTypeInfo);
            cargoVanTypeRepository.save(cargoVanTypeInfo);
        } catch (RuntimeException e) {
            throw new ApiException("Error occurred while updating cargo van type: " + e.getMessage(), StatusCode.CARGODISTR_500);
        }
        return cargoVanTypeMapper.fromEntityToDto(cargoVanTypeInfo);
    }

    private CargoVanTypeInfo findCargoVanTypeById(String id, StatusCode exceptionStatusCode) {
        return cargoVanTypeRepository
                .findById(UUID.fromString(id))
                .orElseThrow(() -> new ApiException("Cargo Van Type wasn't found by id", exceptionStatusCode));
    }

    private CargoVanTypeInfo findCargoVanTypeByName(String name) {
        return cargoVanTypeRepository
                .findByName(name)
                .orElseThrow(() -> new ApiException("Cargo Van Type wasn't found by name", StatusCode.CARGODISTR_005));
    }
}
