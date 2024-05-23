package ru.liga.cargodistributor.cargo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CargoItemTypeRepository extends CrudRepository<CargoItemTypeInfo, UUID> {
    Optional<CargoItemTypeInfo> findByName(String name);

    boolean existsByName(String name);

    void deleteByName(String name);
}