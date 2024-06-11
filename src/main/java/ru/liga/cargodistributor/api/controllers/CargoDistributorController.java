package ru.liga.cargodistributor.api.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.liga.cargodistributor.api.dto.DistributeByFileDto;
import ru.liga.cargodistributor.api.dto.ResponseDto;
import ru.liga.cargodistributor.api.enums.StatusCode;
import ru.liga.cargodistributor.api.exceptions.ApiException;
import ru.liga.cargodistributor.api.services.CargoDistributorService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cargoDistributor/distribute")
public class CargoDistributorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CargoDistributorController.class);

    private final CargoDistributorService cargoDistributorService;

    @PostMapping("/byFile")
    public ResponseEntity<ResponseDto> distributeByFile(@Validated @ModelAttribute DistributeByFileDto source) {
        return ResponseEntity.ok(ResponseDto.createResponseDto(cargoDistributorService.distributeByFile(source)));
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
        if (
                statusCode == StatusCode.CARGODISTR_006 ||
                        statusCode == StatusCode.CARGODISTR_007
        ) {
            responseEntity = ResponseEntity.badRequest().body(ResponseDto.createErrorResponseDto(statusCode));
        } else {
            if (statusCode != null) {
                responseEntity = ResponseEntity.internalServerError().body(ResponseDto.createErrorResponseWithMessageDto(statusCode, runtimeException.getMessage()));
            } else {
                responseEntity = ResponseEntity.internalServerError().body(ResponseDto.createUnexpectedErrorResponseDto(runtimeException));
            }
        }
        return responseEntity;
    }
}
