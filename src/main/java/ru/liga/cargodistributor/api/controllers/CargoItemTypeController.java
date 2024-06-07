package ru.liga.cargodistributor.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.liga.cargodistributor.api.dto.ResponseDto;
import ru.liga.cargodistributor.api.enums.StatusCode;
import ru.liga.cargodistributor.api.exceptions.ApiException;
import ru.liga.cargodistributor.api.services.CargoItemTypeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cargoDistributor/cargoItemType")
public class CargoItemTypeController {

    private final CargoItemTypeService cargoItemTypeService;

    @GetMapping("/all")
    public ResponseEntity<ResponseDto> getAllCargoItemTypes() {
        return ResponseEntity.ok(ResponseDto.createResponseDto(cargoItemTypeService.getAllCargoItemTypes()));
    }

    @GetMapping("/byParams")
    public ResponseEntity<ResponseDto> getCargoItemByParams(@RequestParam(required = false) String id, @RequestParam(required = false) String name) {
        return ResponseEntity.ok(ResponseDto.createResponseDto(cargoItemTypeService.getCargoItemByParams(id, name)));
    }

    //todo: интерфейс сервиса, реализация, маппер, dto
    //todo: метод создания, метод обновления, метод удаления

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDto> handleApiException(RuntimeException runtimeException) {
        //todo: выкинуть потом эту логику в сервис
        StatusCode statusCode;
        if (runtimeException.getClass() == ApiException.class) {
            ApiException apiException = (ApiException) runtimeException;
            statusCode = apiException.getStatus();
        } else {
            statusCode = null;
        }
        ResponseEntity<ResponseDto> responseEntity;
        if (statusCode == StatusCode.CARGODISTR_003) {
            responseEntity = ResponseEntity.badRequest().body(ResponseDto.createErrorResponseDto(statusCode));
        } else if (statusCode == StatusCode.CARGODISTR_002) {
            responseEntity = ResponseEntity.noContent().build();
        } else {
            if (statusCode != null) {
                responseEntity = ResponseEntity.internalServerError().body(ResponseDto.createErrorResponseDto(statusCode));
            } else {
                responseEntity = ResponseEntity.internalServerError().body(ResponseDto.createUnexpectedErrorResponseDto(runtimeException));
            }
        }
        return responseEntity;
    }
}
