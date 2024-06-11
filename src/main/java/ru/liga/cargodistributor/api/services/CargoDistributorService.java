package ru.liga.cargodistributor.api.services;

import ru.liga.cargodistributor.api.dto.DistributeByFileDto;
import ru.liga.cargodistributor.api.dto.DistributeByParamsDto;
import ru.liga.cargodistributor.cargo.CargoVanList;

public interface CargoDistributorService {

    CargoVanList distributeByFile(DistributeByFileDto source);

    CargoVanList distributeByParams(DistributeByParamsDto source);
}
