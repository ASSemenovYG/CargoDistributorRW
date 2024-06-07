package ru.liga.cargodistributor.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.liga.cargodistributor.api.dto.ResponseDto;
import ru.liga.cargodistributor.api.services.CargoItemTypeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cargoDistributor/cargoItemType")
public class CargoItemTypeController {
    //todo: add exception handler

    private final CargoItemTypeService cargoItemTypeService;

    @GetMapping("/all")
    public ResponseEntity<ResponseDto> getAllCargoItemTypes() {
        return ResponseEntity.ok(ResponseDto.createResponseDto(cargoItemTypeService.getAllCargoItemTypes()));
    }

    //todo: интерфейс сервиса, реализация, маппер, dto
    //todo: метод поиска по параметрам (id или название), метод создания, метод обновления, метод удаления
}
