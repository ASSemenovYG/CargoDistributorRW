package ru.liga.dcs.cargo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Класс списка грузов, вычитываемых из файла
 */
public class CargoListFromFile implements CargoList {
    private final List<CargoItem> cargo;

    public CargoListFromFile(String filePath) {
        this.cargo = getCargoFromFile(filePath);
    }

    private List<CargoItem> getCargoFromFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty!");
        }

        List<CargoItem> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<CargoItem> cargoFromLine = parseCargoFileLine(line);
                if (cargoFromLine != null && !cargoFromLine.isEmpty()) {
                    result.addAll(cargoFromLine);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private List<CargoItem> parseCargoFileLine(String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }
        return Pattern.compile("\\d+")
                .matcher(line)
                .results()
                .map(MatchResult::group)
                .map(CargoItem::new)
                .toList();
    }

    public List<CargoItem> getCargo() {
        return cargo;
    }

    public boolean isEmptyOrNull() {
        if (cargo == null) {
            return true;
        }
        return cargo.isEmpty();
    }

    public List<String> getCargoItemNames() {
        return this.cargo.stream()
                .map(CargoItem::getName)
                .toList();
    }
}
