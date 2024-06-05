package ru.liga.cargodistributor.cargo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.cargodistributor.cargo.exceptions.CargoItemValidationException;

import java.util.*;

/**
 * Класс элемента груза (посылки)
 */
@JsonAutoDetect
@JsonSubTypes.Type(value = CargoItemType.class, name = "CargoItemType")
public class CargoItem {
    private static final Logger LOGGER = LoggerFactory.getLogger(CargoItem.class);
    private static final int MAX_SIZE = 9;

    private final int length;
    private final int width;
    private final int size;
    private final String name;
    private String legend;

    /**
     * Список координат клеток в кузове грузовой машины, занимаемых посылкой
     */
    private final List<Coordinates> coordinates;

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
        this.coordinates = new ArrayList<>();
    }

    /**
     * @param size   Размер (площадь) посылки
     * @param length Длина посылки
     * @param width  Ширина посылки
     */
    public CargoItem(int size, int length, int width) {
        this(size, length, width, null);
    }

    /**
     * @param size     Размер (площадь) посылки
     * @param length   Длина посылки
     * @param width    Ширина посылки
     * @param cargoVan Грузовой фургон (для валидации не по дефолтным параметрам)
     */
    public CargoItem(int size, int length, int width, CargoVan cargoVan) {
        String validationMessage = validateCargoItemByParams(size, length, width, cargoVan);
        if (!validationMessage.isEmpty()) {
            LOGGER.error(validationMessage);
            throw new CargoItemValidationException(validationMessage);
        }
        this.length = length;
        this.width = width;
        this.size = size;
        this.name = getCargoItemNameByParams(size, length, width);
        this.coordinates = new ArrayList<>();
    }

    /**
     * @param unparsedCargoItem Лист со строками, составляющими посылку
     * @param cargoVan          Грузовой фургон, когда нужны проверки не по дефолтным параметрам фургона
     */
    public CargoItem(LinkedList<String> unparsedCargoItem, CargoVan cargoVan) {
        validateUnparsedCargoItem(unparsedCargoItem, cargoVan);
        this.name = getUnparsedCargoItemName(unparsedCargoItem);
        this.length = unparsedCargoItem.size();
        this.width = unparsedCargoItem.peekFirst().length();
        this.size = this.length * this.width;
        this.coordinates = new ArrayList<>();
    }

    /**
     * @param unparsedCargoItem Лист со строками, составляющими посылку
     */
    public CargoItem(LinkedList<String> unparsedCargoItem) {
        this(unparsedCargoItem, null);
    }

    /**
     * Конструктор для копирования
     *
     * @param cargoItem посылка, которую нужно скопировать
     */
    protected CargoItem(CargoItem cargoItem) {
        this.length = cargoItem.length;
        this.width = cargoItem.width;
        this.size = cargoItem.size;
        this.name = cargoItem.name;
        this.coordinates = new ArrayList<>(cargoItem.getCoordinates());
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

    public String getLegend() {
        return String.valueOf(size);
    }

    public void setLegend(String legend) {
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

    public CargoItem copy() {
        return new CargoItem(this);
    }

    public void fillCoordinatesByCargoVan(CargoVan cargoVan) {
        CargoVan.CargoVanCell[][] cargo = cargoVan.getCargo();
        for (int i = 0; i < cargoVan.getLength(); i++) {
            for (int j = 0; j < cargoVan.getWidth(); j++) {
                if (!cargo[i][j].isEmpty() && cargo[i][j].getOccupiedBy() == this) {
                    coordinates.add(new Coordinates(i, j));
                }
            }
        }
    }

    private String validateCargoItemByParams(int size, int length, int width, CargoVan cargoVan) {
        int vanLength = (cargoVan == null ? CargoVan.DEFAULT_VAN_LENGTH : cargoVan.getLength());
        int vanWidth = (cargoVan == null ? CargoVan.DEFAULT_VAN_WIDTH : cargoVan.getWidth());
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
        if (length > vanLength) {
            validationMessage.append("Длина посылки ").append(length).append(" превышает длину грузового фургона ").append(vanLength).append("\n");
        }
        if (width > vanWidth) {
            validationMessage.append("Ширина посылки ").append(length).append(" превышает ширину грузового фургона ").append(vanWidth).append("\n");
        }
        return validationMessage.toString();
    }

    private void validateUnparsedCargoItem(LinkedList<String> unparsedCargoItem, CargoVan cargoVan) {
        String firstLine = unparsedCargoItem.peekFirst();
        if (firstLine == null || firstLine.isEmpty()) {
            LOGGER.error("В посылке не может быть пустых или null строк");
            throw new CargoItemValidationException("В посылке не может быть пустых или null строк");
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

        validationMessage.append(validateCargoItemByParams(cargoItemSize, cargoItemLength, cargoItemWidth, cargoVan));

        if (!validationMessage.isEmpty()) {
            validationMessage.insert(0, "Во входных данных обнаружена невалидная посылка:\n" + getUnparsedCargoItemName(unparsedCargoItem) + "\n");
            LOGGER.error(validationMessage.toString());
            throw new CargoItemValidationException(validationMessage.toString());
        }
    }

    private String getUnparsedCargoItemName(LinkedList<String> unparsedCargoItem) {
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
