package ru.liga.cargodistributor.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCode {
    CARGODISTR_001("CARGODISTR_001", "Success"),
    CARGODISTR_002("CARGODISTR_002", "Cargo Type Item Not found"),
    CARGODISTR_003("CARGODISTR_003", "At least one of query parameters is required: id or name"),
    CARGODISTR_004("CARGODISTR_004", "Cargo Item Type successfully deleted"),
    CARGODISTR_404("CARGODISTR_404", "Cargo Type Item Not exists"),
    CARGODISTR_500("CARGODISTR_500", "Internal Server Error");

    private final String code;
    private final String message;
}
