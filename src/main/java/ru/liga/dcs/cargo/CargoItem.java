package ru.liga.dcs.cargo;

import java.util.LinkedList;
import java.util.Objects;

/**
 * Класс элемента груза (посылки)
 */
public class CargoItem {

    @Deprecated
    private static final int MAX_LENGTH = 6;
    //TODO: Тут еще нужна будет ширина и/или список координат в кузове
    private static final int MAX_SIZE = 9;
    private final int length;
    private final int width;
    private final String name;

    public CargoItem(int size, int length, int width) {
        if (size <= 0) {
            throw new IllegalArgumentException("Размер посылки должен быть больше нуля, переданный размер: " + size);
        }
        if (size > MAX_SIZE) {
            throw new IllegalArgumentException("Размер посылки не может превышать " + MAX_SIZE + "; переданный размер: " + size);
        }
        if (length <= 0) {
            throw new IllegalArgumentException("Длина посылки должна быть больше нуля, переданная длина: " + length);
        }
        if (width <= 0) {
            throw new IllegalArgumentException("Ширина посылки должна быть больше нуля, переданная ширина: " + width);
        }
        if (size != length * width) {
            throw new IllegalArgumentException("Некорректные параметры посылки, размер " + size + " не соответствует длине " + length + " и ширине " + width);
        }
        if (length > CargoVan.VAN_LENGTH) {
            throw new IllegalArgumentException("Длина посылки " + length + " превышает длину грузового фургона " + CargoVan.VAN_LENGTH);
        }
        if (width > CargoVan.VAN_WIDTH) {
            throw new IllegalArgumentException("Ширина посылки " + length + " превышает ширину грузового фургона " + CargoVan.VAN_LENGTH);
        }
        this.length = length;
        this.width = width;
        this.name = getCargoItemNameByParams(size, length, width);
    }

    /**
     * @param unparsedCargoItem Лист со строками, составляющими посылку
     */
    public CargoItem(LinkedList<String> unparsedCargoItem) {
        validateUnparsedCargoItem(unparsedCargoItem);
        this.name = getUnparsedCargoItem(unparsedCargoItem);
        this.length = unparsedCargoItem.size();
        this.width = unparsedCargoItem.peekFirst().length();
    }

    @Deprecated
    public CargoItem(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Cargo item name cannot be null or empty!");
        }
        if (name.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Max length of cargo item cannot be greater than " + MAX_LENGTH + "! Provided length: " +
                    name.length() + "; Cargo name : " + name);
        }
        this.name = name;
        this.length = name.length();
        this.width = 0;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    private void validateUnparsedCargoItem(LinkedList<String> unparsedCargoItem) {
        String firstLine = unparsedCargoItem.peekFirst();
        if (firstLine == null || firstLine.isEmpty()) {
            throw new IllegalArgumentException("В посылке не может быть пустых или null строк");
        }
        int cargoItemSize = Integer.parseInt(firstLine.substring(0, 1));
        int cargoItemWidth = firstLine.length();
        StringBuilder validationMessage = new StringBuilder();

        if (!unparsedCargoItem.stream().allMatch(s -> Objects.equals(s, firstLine))) {
            validationMessage
                    .append("Посылки могут быть только прямоугольными.")
                    .append("\n");
        }

        if (validationMessage.isEmpty()) {
            if (cargoItemWidth > CargoVan.VAN_WIDTH) {
                validationMessage
                        .append("Ширина посылки ")
                        .append(cargoItemWidth)
                        .append("превышает ширину грузового фургона, равную ")
                        .append(CargoVan.VAN_WIDTH)
                        .append("\n");
            }

            if (unparsedCargoItem.size() > CargoVan.VAN_LENGTH) {
                validationMessage
                        .append("Длина посылки ")
                        .append(cargoItemWidth)
                        .append("превышает длину грузового фургона, равную ")
                        .append(CargoVan.VAN_LENGTH)
                        .append("\n");
            }

            if (unparsedCargoItem.size() * cargoItemWidth != cargoItemSize) {
                validationMessage
                        .append("Площадь посылки, равная ")
                        .append(unparsedCargoItem.size() * cargoItemWidth)
                        .append(" не соответствует заявленной площади посылки, равной ")
                        .append(cargoItemSize)
                        .append("\n");
            }
        }

        if (!validationMessage.isEmpty()) {
            validationMessage.insert(0, "Во входных данных обнаружена невалидная посылка:\n" + getUnparsedCargoItem(unparsedCargoItem) + "\n");
            throw new IllegalArgumentException(validationMessage.toString());
        }
    }

    private String getUnparsedCargoItem(LinkedList<String> unparsedCargoItem) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < unparsedCargoItem.size(); i++) {
            result.append(unparsedCargoItem.get(i));
            if (i < unparsedCargoItem.size() - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }

    private String getCargoItemNameByParams(int size, int length, int width) {
        String title = Integer.toString(size);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(title.repeat(width));
            if (i < length - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }
}
