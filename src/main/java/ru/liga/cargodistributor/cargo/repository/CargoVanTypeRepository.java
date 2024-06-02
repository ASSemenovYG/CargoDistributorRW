package ru.liga.cargodistributor.cargo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.liga.cargodistributor.cargo.entity.CargoVanTypeInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CargoVanTypeRepository extends CrudRepository<CargoVanTypeInfo, UUID> {
    Optional<CargoVanTypeInfo> findByName(String name);

    boolean existsByName(String name);

    void deleteByName(String name);

    List<CargoVanTypeInfo> findAll();
}