package ru.liga.dcs.cargo;

/**
 * Класс элемента груза (посылки)
 */
public class CargoItem {
    private static final int MAX_LENGTH = 6;
    //TODO: Тут еще нужна будет ширина и/или список координат в кузове
    private final int length;
    private final String name;

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
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }
}
