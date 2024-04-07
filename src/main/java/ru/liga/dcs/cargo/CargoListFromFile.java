package ru.liga.dcs.cargo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Класс, реализующий интерфейс {@link CargoList},
 * предоставляющий возможность вычитки списка посылок из файла
 */
public class CargoListFromFile implements CargoList {
    private final List<CargoItem> cargo;
    private final String filePath;
    private final List<LinkedList<String>> linesWithCargoItems;

    /**
     * @param filePath Путь к файлу, из которого нужно вычитать список посылок
     */
    public CargoListFromFile(String filePath) {
        this.filePath = filePath;
        this.linesWithCargoItems = null;
        this.cargo = getCargoFromFile();

    }

    /**
     * @param linesWithCargoItems Лист со списками строк, составляющих посылку
     */
    public CargoListFromFile(List<LinkedList<String>> linesWithCargoItems) {
        this.filePath = null;
        this.linesWithCargoItems = linesWithCargoItems;
        this.cargo = getCargoFromFile();

    }

    private List<CargoItem> getCargoFromFile() {
        if (filePath == null) {
            return processUnparsedCargoItems(linesWithCargoItems);
        }
        return processUnparsedCargoItems(parseFileLines(filePath));
    }

    private List<CargoItem> processUnparsedCargoItems(List<LinkedList<String>> unparsedCargoItems) {
        List<CargoItem> result = new ArrayList<>();
        for (LinkedList<String> unparsedCargoItem : unparsedCargoItems) {
            result.add(new CargoItem(unparsedCargoItem));
        }
        return result;
    }

    private List<LinkedList<String>> parseFileLines(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty!");
        }
        List<LinkedList<String>> fileLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            LinkedList<String> unparsedCargoItem = new LinkedList<>();
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    if (!unparsedCargoItem.isEmpty()) {
                        fileLines.add(new LinkedList<>(unparsedCargoItem));
                        unparsedCargoItem.clear();
                    }
                } else {
                    unparsedCargoItem.add(line);
                }
            }
            if (!unparsedCargoItem.isEmpty()) {
                fileLines.add(new LinkedList<>(unparsedCargoItem));
                unparsedCargoItem.clear();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileLines;
    }

    @Deprecated
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

    public void printCargoItems() {
        for (CargoItem cargoItem : cargo) {
            System.out.println(cargoItem.getName());
            System.out.println("\r");
        }
    }
}
