package ru.liga.cargodistributor.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCode {
    CARGODISTR_001("CARGODISTR_001", "Success");

    private final String code;
    private final String message;
}
