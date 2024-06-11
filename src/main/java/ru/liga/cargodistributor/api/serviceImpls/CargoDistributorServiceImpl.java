package ru.liga.cargodistributor.api.serviceImpls;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.liga.cargodistributor.algorithm.CargoDistributionParameters;
import ru.liga.cargodistributor.algorithm.services.DistributionAlgorithmService;
import ru.liga.cargodistributor.api.dto.CargoItemTypeParamsDto;
import ru.liga.cargodistributor.api.dto.CargoVanTypeParamsDto;
import ru.liga.cargodistributor.api.dto.DistributeByFileDto;
import ru.liga.cargodistributor.api.dto.DistributeByParamsDto;
import ru.liga.cargodistributor.api.enums.StatusCode;
import ru.liga.cargodistributor.api.exceptions.ApiException;
import ru.liga.cargodistributor.api.services.CargoDistributorService;
import ru.liga.cargodistributor.cargo.*;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;
import ru.liga.cargodistributor.cargo.entity.CargoVanTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoItemTypeRepository;
import ru.liga.cargodistributor.cargo.repository.CargoVanTypeRepository;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CargoDistributorServiceImpl implements CargoDistributorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CargoDistributorServiceImpl.class);

    private final FileService fileService;
    private final CargoConverterService cargoConverterService;
    private final CargoItemTypeRepository cargoItemTypeRepository;
    private final CargoVanTypeRepository cargoVanTypeRepository;

    @Override
    public CargoVanList distributeByFile(DistributeByFileDto source) {
        DistributionAlgorithmService algorithmService = DistributionAlgorithmService.createServiceByName(source.getAlgorithmName());
        int vanLimit = source.getVanLimit();
        CargoItemList cargoItemList;

        try {
            String fileContent = fileService.readFromFile(fileService.multipartFileToFile(source.getCargoItemListFile()));
            cargoItemList = new CargoItemList(cargoConverterService.parseCargoItems(fileContent));
        } catch (RuntimeException e) {
            LOGGER.error("Error occurred while creating cargo list from file: {}", e.getMessage());
            throw new ApiException(e.getMessage(), StatusCode.CARGODISTR_500);
        }

        CargoVanList cargoVanList = new CargoVanList();
        cargoVanList.distributeCargo(algorithmService, cargoItemList);
        validateDistributionLimit(vanLimit, cargoVanList);
        return cargoVanList;
    }

    @Override
    public CargoVanList distributeByParams(DistributeByParamsDto source) {
        int vanLimit = source.getVanLimit();
        CargoVan cargoVan = new CargoVanType(findCargoVanTypeByParams(source.getCargoVanType()));
        LinkedList<CargoDistributionParameters.CargoItemToLoad> cargoItemsToLoad = parseCargoItemTypesToLoad(source.getCargoItemTypes(), cargoVan);

        CargoDistributionParameters cargoDistributionParameters = new CargoDistributionParameters(
                cargoVan,
                cargoItemsToLoad,
                vanLimit,
                source.getAlgorithmName()
        );

        try {
            cargoDistributionParameters.validateItemListForDuplicateLegends();
        } catch (RuntimeException e) {
            LOGGER.error("distributeByParams: {}", e.getMessage());
            throw new ApiException(e.getMessage(), StatusCode.CARGODISTR_008);
        }

        CargoVanList cargoVanList = new CargoVanList();
        cargoVanList.distributeCargoByParameters(cargoDistributionParameters);
        validateDistributionLimit(vanLimit, cargoVanList);
        return cargoVanList;
    }

    private void validateDistributionLimit(int vanLimit, CargoVanList cargoVanList) {
        if (!cargoVanList.isListSizeLessOrEqualThanMaxSize(vanLimit)) {
            LOGGER.info("Entered Van Limit {} Wasn't Enough For Distribution, actual count of loaded vans: {}", vanLimit, cargoVanList.getCargoVans().size());
            throw new ApiException("Entered Van Limit Wasn't Enough For Distribution", StatusCode.CARGODISTR_007);
        }
    }

    private LinkedList<CargoDistributionParameters.CargoItemToLoad> parseCargoItemTypesToLoad(List<CargoItemTypeParamsDto> source, CargoVan cargoVan) {
        LinkedList<CargoDistributionParameters.CargoItemToLoad> cargoItemsToLoad = new LinkedList<>();
        for (CargoItemTypeParamsDto params : source) {
            CargoItemTypeInfo cargoItemTypeInfo = findCargoItemTypeByParams(params);
            CargoDistributionParameters.CargoItemToLoad cargoItemToLoad = new CargoDistributionParameters.CargoItemToLoad(
                    new CargoItemType(cargoItemTypeInfo, cargoVan),
                    params.getCount()
            );
            if (params.getOverrideLegend() != null && !params.getOverrideLegend().isBlank() && !params.getOverrideLegend().isEmpty()) {
                cargoItemToLoad.getCargoItem().setLegend(params.getOverrideLegend());
            }
            cargoItemsToLoad.add(cargoItemToLoad);
        }
        return cargoItemsToLoad;
    }

    //todo: методы ниже повторяются в соседних сервисах, надо вынести код в отдельный класс
    private CargoItemTypeInfo findCargoItemTypeByParams(CargoItemTypeParamsDto source) {
        String id = (source.getId() != null) ? String.valueOf(source.getId()) : null;
        String name = source.getName();
        CargoItemTypeInfo cargoItemTypeInfo;
        if (id != null && !id.isEmpty() && !id.isBlank()) {
            cargoItemTypeInfo = findCargoItemTypeById(id, StatusCode.CARGODISTR_002);
        } else if (name != null && !name.isEmpty() && !name.isBlank()) {
            cargoItemTypeInfo = findCargoItemTypeByName(name);
        } else {
            throw new ApiException("At least one of query parameters is required: id or name", StatusCode.CARGODISTR_003);
        }
        return cargoItemTypeInfo;
    }

    private CargoItemTypeInfo findCargoItemTypeById(String id, StatusCode exceptionStatusCode) {
        return cargoItemTypeRepository
                .findById(UUID.fromString(id))
                .orElseThrow(() -> new ApiException("Cargo Item Type wasn't found by id", exceptionStatusCode));
    }

    private CargoItemTypeInfo findCargoItemTypeByName(String name) {
        return cargoItemTypeRepository
                .findByName(name)
                .orElseThrow(() -> new ApiException("Cargo Item Type wasn't found by name", StatusCode.CARGODISTR_002));
    }

    private CargoVanTypeInfo findCargoVanTypeByParams(CargoVanTypeParamsDto source) {
        String id = (source.getId() != null) ? String.valueOf(source.getId()) : null;
        String name = source.getName();
        CargoVanTypeInfo cargoVanTypeInfo;
        if (id != null && !id.isEmpty() && !id.isBlank()) {
            cargoVanTypeInfo = findCargoVanTypeById(id, StatusCode.CARGODISTR_005);
        } else if (name != null && !name.isEmpty() && !name.isBlank()) {
            cargoVanTypeInfo = findCargoVanTypeByName(name);
        } else {
            throw new ApiException("At least one of query parameters is required: id or name", StatusCode.CARGODISTR_003);
        }
        return cargoVanTypeInfo;
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
