package ru.liga.cargodistributor.api.serviceImpls;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.liga.cargodistributor.algorithm.services.DistributionAlgorithmService;
import ru.liga.cargodistributor.api.dto.DistributeByFileDto;
import ru.liga.cargodistributor.api.enums.StatusCode;
import ru.liga.cargodistributor.api.exceptions.ApiException;
import ru.liga.cargodistributor.api.services.CargoDistributorService;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.CargoVanList;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

@RequiredArgsConstructor
@Service
public class CargoDistributorServiceImpl implements CargoDistributorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CargoDistributorServiceImpl.class);

    private final FileService fileService;
    private final CargoConverterService cargoConverterService;

    @Override
    public CargoVanList distributeByFile(DistributeByFileDto source) {
        DistributionAlgorithmService algorithmService = DistributionAlgorithmService.createServiceByName(source.getAlgorithmName());
        int vanLimit = source.getVanLimit();
        if (vanLimit < 1) {
            throw new ApiException("Van Limit Should be Greater Than Zero", StatusCode.CARGODISTR_006);
        }

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

        if (cargoVanList.getCargoVans().size() > vanLimit) {
            LOGGER.info("Entered Van Limit {} Wasn't Enough For Distribution, actual count of loaded vans: {}", vanLimit, cargoVanList.getCargoVans().size());
            throw new ApiException("Entered Van Limit Wasn't Enough For Distribution", StatusCode.CARGODISTR_007);
        }
        return cargoVanList;
    }

    @Override
    public CargoVanList distributeByParams() {
        return null;
    }
}
