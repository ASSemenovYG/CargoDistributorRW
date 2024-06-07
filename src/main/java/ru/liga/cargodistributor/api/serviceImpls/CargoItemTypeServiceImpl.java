package ru.liga.cargodistributor.api.serviceImpls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.liga.cargodistributor.api.dto.CargoItemTypeInfoDto;
import ru.liga.cargodistributor.api.mapper.CargoItemTypeMapper;
import ru.liga.cargodistributor.api.services.CargoItemTypeService;
import ru.liga.cargodistributor.cargo.repository.CargoItemTypeRepository;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CargoItemTypeServiceImpl implements CargoItemTypeService {

    private final CargoItemTypeRepository cargoItemTypeRepository;
    private final CargoItemTypeMapper cargoItemTypeMapper;

    @Override
    public Set<CargoItemTypeInfoDto> getAllCargoItemTypes() {
        return new HashSet<>(cargoItemTypeMapper.toDtoList(cargoItemTypeRepository.findAll()));
    }
}
