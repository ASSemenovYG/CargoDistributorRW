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
import ru.liga.cargodistributor.api.services.CargoItemTypeService;
import ru.liga.cargodistributor.api.services.CargoVanTypeService;
import ru.liga.cargodistributor.cargo.*;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;
import ru.liga.cargodistributor.cargo.entity.CargoVanTypeInfo;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CargoDistributorServiceImpl implements CargoDistributorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CargoDistributorServiceImpl.class);

    private final FileService fileService;
    private final CargoConverterService cargoConverterService;
    private final CargoItemTypeService cargoItemTypeService;
    private final CargoVanTypeService cargoVanTypeService;

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

    private CargoItemTypeInfo findCargoItemTypeByParams(CargoItemTypeParamsDto source) {
        String id = (source.getId() != null) ? String.valueOf(source.getId()) : null;
        String name = source.getName();
        return cargoItemTypeService.findCargoItemTypeInfoByParams(id, name);
    }

    private CargoVanTypeInfo findCargoVanTypeByParams(CargoVanTypeParamsDto source) {
        String id = (source.getId() != null) ? String.valueOf(source.getId()) : null;
        String name = source.getName();
        return cargoVanTypeService.findCargoVanTypeInfoByParams(id, name);
    }
}
