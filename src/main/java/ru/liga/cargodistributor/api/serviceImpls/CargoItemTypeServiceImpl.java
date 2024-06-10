package ru.liga.cargodistributor.api.serviceImpls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.liga.cargodistributor.api.dto.CargoItemTypeInfoCreateDto;
import ru.liga.cargodistributor.api.dto.CargoItemTypeInfoDto;
import ru.liga.cargodistributor.api.enums.StatusCode;
import ru.liga.cargodistributor.api.exceptions.ApiException;
import ru.liga.cargodistributor.api.mapper.CargoItemTypeMapper;
import ru.liga.cargodistributor.api.services.CargoItemTypeService;
import ru.liga.cargodistributor.cargo.CargoItemType;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoItemTypeRepository;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CargoItemTypeServiceImpl implements CargoItemTypeService {
    //todo: add logging

    private final CargoItemTypeRepository cargoItemTypeRepository;
    private final CargoItemTypeMapper cargoItemTypeMapper;
    private final FileService fileService;

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

    @Override
    @Transactional
    public CargoItemTypeInfoDto createCargoItemTypeInfo(CargoItemTypeInfoCreateDto source) {
        try {
            //todo: разделить эксепшены валидации от возможных системных ошибок, добавить явные проверки, кроме создания CargoItemType
            String fileContent = fileService.readFromFile(fileService.multipartFileToFile(source.getMultipartFile()));
            CargoItemTypeInfo cargoItemTypeInfo = cargoItemTypeMapper.createEntityFromDto(source);
            cargoItemTypeInfo.setShape(fileContent);
            CargoItemType cargoItemType = new CargoItemType(cargoItemTypeInfo);
            cargoItemTypeRepository.save(cargoItemTypeInfo);
            return cargoItemTypeMapper.fromEntityToDto(cargoItemTypeInfo);
        } catch (RuntimeException e) {
            throw new ApiException("Error occurred while creating cargo item type: " + e.getMessage(), StatusCode.CARGODISTR_500);
        }
    }

    @Override
    @Transactional
    public CargoItemTypeInfoDto updateCargoItemTypeInfo(String id, CargoItemTypeInfoCreateDto source) {
        CargoItemTypeInfo cargoItemTypeInfo = findCargoItemTypeById(id, StatusCode.CARGODISTR_404);
        //todo: разделить эксепшены валидации от возможных системных ошибок, добавить явные проверки, кроме создания CargoItemType
        try {
            if (source.getName() != null && !source.getName().isEmpty() && !source.getName().isBlank()) {
                cargoItemTypeInfo.setName(source.getName());
            }

            if (source.getLegend() != null && !source.getLegend().isEmpty() && !source.getLegend().isBlank()) {
                cargoItemTypeInfo.setLegend(source.getLegend());
            }

            if (source.getMultipartFile() != null) {
                String fileContent = fileService.readFromFile(fileService.multipartFileToFile(source.getMultipartFile()));
                cargoItemTypeInfo.setShape(fileContent);
            }

            new CargoItemType(cargoItemTypeInfo);
        } catch (RuntimeException e) {
            throw new ApiException("Error occurred while updating cargo item type: " + e.getMessage(), StatusCode.CARGODISTR_500);
        }

        cargoItemTypeRepository.save(cargoItemTypeInfo);
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
