package ru.liga.cargodistributor.api.exceptions;

import lombok.Getter;
import ru.liga.cargodistributor.api.enums.StatusCode;

@Getter
public class ApiException extends RuntimeException {
    private StatusCode status;

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, StatusCode statusCode) {
        super(message);
        this.status = statusCode;
    }
}
