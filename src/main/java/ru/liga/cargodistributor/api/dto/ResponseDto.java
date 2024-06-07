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

    public static ResponseDto createResponseDto(Object data) {
        return new ResponseDto(data, StatusCode.CARGODISTR_001);
    }
}