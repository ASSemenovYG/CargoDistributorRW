package ru.liga.cargodistributor.api.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.liga.cargodistributor.api.dto.CargoVanTypeInfoDto;
import ru.liga.cargodistributor.api.dto.ResponseDto;
import ru.liga.cargodistributor.api.enums.StatusCode;
import ru.liga.cargodistributor.api.exceptions.ApiException;
import ru.liga.cargodistributor.api.services.CargoVanTypeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cargoDistributor/cargoVanType")
public class CargoVanTypeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CargoVanTypeController.class);

    private final CargoVanTypeService cargoVanTypeService;

    @GetMapping("/all")
    public ResponseEntity<ResponseDto> getAllCargoVanTypes() {
        return ResponseEntity.ok(ResponseDto.createResponseDto(cargoVanTypeService.getAllCargoVanTypes()));
    }

    @GetMapping("/byParams")
    public ResponseEntity<ResponseDto> getCargoVanTypeByParams(@RequestParam(required = false) String id, @RequestParam(required = false) String name) {
        return ResponseEntity.ok(ResponseDto.createResponseDto(cargoVanTypeService.getCargoVanTypeByParams(id, name)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteCargoVanTypeById(@PathVariable("id") String id) {
        return ResponseEntity.ok(ResponseDto.createResponseDtoOnDelete(cargoVanTypeService.deleteCargoVanTypeById(id)));
    }

    @PostMapping
    public ResponseEntity<ResponseDto> createCargoVanType(@Validated(CargoVanTypeInfoDto.New.class) @RequestBody CargoVanTypeInfoDto source) {
        return ResponseEntity.ok(ResponseDto.createResponseDto(cargoVanTypeService.createCargoVanTypeInfo(source)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDto> updateCargoVanType(
            @PathVariable("id") String id,
            @Validated(CargoVanTypeInfoDto.Update.class)
            @RequestBody CargoVanTypeInfoDto source
    ) {
        return ResponseEntity.ok(ResponseDto.createResponseDto(cargoVanTypeService.updateCargoVanTypeInfo(id, source)));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDto> handleApiException(RuntimeException runtimeException) {
        LOGGER.error("handleApiException: {}", runtimeException.getMessage());
        //todo: выкинуть потом эту логику из контроллера
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
        } else if (statusCode == StatusCode.CARGODISTR_005) {
            responseEntity = ResponseEntity.noContent().build();
        } else if (statusCode == StatusCode.CARGODISTR_404) {
            responseEntity = ResponseEntity.notFound().build();
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
