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

    /**
     * @param size Размер (площадь) посылки
     * @param length Длина посылки
     * @param width Ширина посылки
     */
    public CargoItem(int size, int length, int width) {
        String validationMessage = validateCargoItemByParams(size, length, width);
        if (!validationMessage.isEmpty()) {
            throw new IllegalArgumentException(validationMessage);
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

    private String validateCargoItemByParams(int size, int length, int width) {
        StringBuilder validationMessage = new StringBuilder();
        if (size <= 0) {
            validationMessage.append("Размер посылки должен быть больше нуля, переданный размер: ").append(size).append("\n");
        }
        if (size > MAX_SIZE) {
            validationMessage.append("Размер посылки не может превышать ").append(MAX_SIZE).append("; переданный размер: ").append(size).append("\n");
        }
        if (length <= 0) {
            validationMessage.append("Длина посылки должна быть больше нуля, переданная длина: ").append(length).append("\n");
        }
        if (width <= 0) {
            validationMessage.append("Ширина посылки должна быть больше нуля, переданная ширина: ").append(width).append("\n");
        }
        if (size != length * width) {
            validationMessage.append("Некорректные параметры посылки, размер ").append(size).append(" не соответствует длине ").append(length)
                    .append(" и ширине ").append(width).append("\n");
        }
        if (length > CargoVan.VAN_LENGTH) {
            validationMessage.append("Длина посылки ").append(length).append(" превышает длину грузового фургона ").append(CargoVan.VAN_LENGTH).append("\n");
        }
        if (width > CargoVan.VAN_WIDTH) {
            validationMessage.append("Ширина посылки ").append(length).append(" превышает ширину грузового фургона ").append(CargoVan.VAN_WIDTH).append("\n");
        }
        return validationMessage.toString();
    }

    private void validateUnparsedCargoItem(LinkedList<String> unparsedCargoItem) {
        String firstLine = unparsedCargoItem.peekFirst();
        if (firstLine == null || firstLine.isEmpty()) {
            throw new IllegalArgumentException("В посылке не может быть пустых или null строк");
        }
        int cargoItemSize = Integer.parseInt(firstLine.substring(0, 1));
        int cargoItemLength = unparsedCargoItem.size();
        int cargoItemWidth = firstLine.length();
        StringBuilder validationMessage = new StringBuilder();

        if (!unparsedCargoItem.stream().allMatch(s -> Objects.equals(s, firstLine))) {
            validationMessage
                    .append("Посылки могут быть только прямоугольными.")
                    .append("\n");
        }

        validationMessage.append(validateCargoItemByParams(cargoItemSize, cargoItemLength, cargoItemWidth));

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
