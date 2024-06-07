package ru.liga.cargodistributor.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.liga.cargodistributor.api.enums.StatusCode;

@Data
@AllArgsConstructor
@Builder
public class ResponseDto<T> {
    @JsonProperty("data")
    private T data;
    @JsonProperty("result")
    private ResponseResultDto responseResultDto;

    public ResponseDto(T data, StatusCode statusCode) {
        this.data = data;
        this.responseResultDto = new ResponseResultDto(statusCode, statusCode.getMessage());
    }

    public ResponseDto(RuntimeException exception) {
        this.data = null;
        this.responseResultDto = new ResponseResultDto(StatusCode.CARGODISTR_500, exception.getMessage());
    }

    public static ResponseDto createResponseDto(Object data) {
        return new ResponseDto(data, StatusCode.CARGODISTR_001);
    }

    public static ResponseDto createErrorResponseDto(StatusCode statusCode) {
        return new ResponseDto(null, statusCode);
    }

    public static ResponseDto createUnexpectedErrorResponseDto(RuntimeException exception) {
        return new ResponseDto(exception);
    }
}