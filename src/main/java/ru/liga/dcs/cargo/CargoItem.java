package ru.liga.dcs.cargo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Класс элемента груза (посылки)
 */
@JsonAutoDetect
public class CargoItem {
    private static final Logger LOGGER = LogManager.getLogger(CargoItem.class);
    @Deprecated
    private static final int MAX_LENGTH = 6;
    private static final int MAX_SIZE = 9;
    private final int length;
    private final int width;
    private final int size;
    private final String name;
    /**
     * Список координат клеток в кузове грузовой машины, занимаемых посылкой
     */
    private final List<Coordinates> coordinates = new ArrayList<>();

    /**
     * Координата клетки в кузове грузовой машины
     */
    public static class Coordinates {
        private final int x;
        private final int y;

        /**
         * Конструктор для десериализации
         */
        public Coordinates() {
            this.x = 0;
            this.y = 0;
        }

        public Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (getClass() != o.getClass()) {
                return false;
            }
            Coordinates other = (Coordinates) o;
            if (this.x != other.getX()) {
                return false;
            }
            if (this.y != other.getY()) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    /**
     * Конструктор для десериализации
     */
    public CargoItem() {
        this.length = 0;
        this.width = 0;
        this.size = 0;
        this.name = null;
    }

    /**
     * @param size   Размер (площадь) посылки
     * @param length Длина посылки
     * @param width  Ширина посылки
     */
    public CargoItem(int size, int length, int width) {
        String validationMessage = validateCargoItemByParams(size, length, width);
        if (!validationMessage.isEmpty()) {
            LOGGER.error(validationMessage);
            throw new IllegalArgumentException(validationMessage);
        }
        this.length = length;
        this.width = width;
        this.size = size;
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
        this.size = this.length * this.width;
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
        this.size = 0;
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

    public int getSize() {
        return size;
    }

    public List<Coordinates> getCoordinates() {
        return coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (getClass() != o.getClass()) {
            return false;
        }
        CargoItem other = (CargoItem) o;
        if (!Objects.equals(this.name, other.getName())) {
            return false;
        }
        if (this.width != other.getWidth()) {
            return false;
        }
        if (this.length != other.getLength()) {
            return false;
        }
        if (this.size != other.getSize()) {
            return false;
        }
        List<Coordinates> otherCoordinates = other.getCoordinates();
        if (this.coordinates.size() != otherCoordinates.size()) {
            return false;
        }
        return new HashSet<>(this.coordinates).containsAll(otherCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, width, length, size, coordinates);
    }

    public void fillCoordinatesByCargoVan(CargoVan cargoVan) {
        CargoVan.CargoVanCell[][] cargo = cargoVan.getCargo();
        for (int i = 0; i < CargoVan.VAN_LENGTH; i++) {
            for (int j = 0; j < CargoVan.VAN_WIDTH; j++) {
                if (!cargo[i][j].isEmpty() && cargo[i][j].getOccupiedBy() == this) {
                    coordinates.add(new Coordinates(i, j));
                }
            }
        }
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
            LOGGER.error("В посылке не может быть пустых или null строк");
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
            LOGGER.error(validationMessage.toString());
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
