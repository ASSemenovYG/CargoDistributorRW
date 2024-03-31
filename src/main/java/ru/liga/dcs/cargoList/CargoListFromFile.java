package ru.liga.dcs.cargoList;

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
    private final List<String> cargo;

    public CargoListFromFile(String filePath) {
        this.cargo = getCargoFromFile(filePath);
    }

    private List<String> getCargoFromFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty!");
        }

        List<String> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> cargoFromLine = parseCargoFileLine(line);
                if (cargoFromLine != null && !cargoFromLine.isEmpty()) {
                    result.addAll(cargoFromLine);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private List<String> parseCargoFileLine(String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }
        return Pattern.compile("\\d+")
                .matcher(line)
                .results()
                .map(MatchResult::group)
                .toList();
    }

    public List<String> getCargo() {
        return cargo;
    }

    public boolean isEmpty() {
        return cargo.isEmpty();
    }
}
