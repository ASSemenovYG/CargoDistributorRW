package ru.liga.CargoDistributor;

import ru.liga.CargoDistributor.algorithm.DistributionAlgorithm;
import ru.liga.CargoDistributor.algorithm.OneVanOneItemDistribution;
import ru.liga.CargoDistributor.algorithm.SimpleFitDistribution;
import ru.liga.CargoDistributor.algorithm.SingleSortedCargoDistribution;
import ru.liga.CargoDistributor.cargo.CargoList;
import ru.liga.CargoDistributor.cargo.CargoListFromFile;
import ru.liga.CargoDistributor.cargo.CargoVanList;
import ru.liga.CargoDistributor.cargo.CargoVanToJsonConverter;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        System.out.println("\nПожалуйста, введите число, чтобы выбрать желаемую функцию:");
        System.out.println("1 : Чтение списка загруженных фургонов из файла");
        System.out.println("2 : Чтение списка посылок из файла и распределение по грузовым фургонам (default)");
        int functionCode = console.nextInt();
        switch (functionCode) {
            case 1 -> processCargoVansFromFile();
            default -> processCargoFromFile();
        }
    }

    private static void processCargoFromFile() {
        Scanner console = new Scanner(System.in);
        System.out.println("Введите путь к файлу с посылками:");
        String filePath = console.nextLine();
        CargoList cargoList = new CargoListFromFile(filePath);

        if (cargoList.isEmptyOrNull()) {
            System.out.println("В файле не найдено ни одной посылки!");
            return;
        }
        System.out.println("В файле найдены следующие посылки:");
        cargoList.printCargoItems();
        System.out.println("Пожалуйста, введите число, чтобы выбрать желаемый алгоритм распределения:");
        System.out.println("1 : OneVanOneItemDistribution");
        System.out.println("2 : SingleSortedCargoDistribution");
        System.out.println("3 : SimpleFitDistribution (default)");
        int algorithmCode = console.nextInt();
        System.out.println("Пожалуйста, укажите максимальное количество грузовых фургонов для распределения:");
        int maxCargoVanCount = console.nextInt();

        DistributionAlgorithm algorithm = switch (algorithmCode) {
            case 1 -> new OneVanOneItemDistribution(cargoList);
            case 2 -> new SingleSortedCargoDistribution(cargoList);
            default -> new SimpleFitDistribution(cargoList);
        };

        algorithm.checkIfLoadedVansCountLessThanMaxCount(maxCargoVanCount);
        System.out.println("Результат распределения посылок по грузовым фургонам:");
        algorithm.printLoadedVans();
        CargoVanToJsonConverter converter = new CargoVanToJsonConverter();
        String jsonFileName = converter.writeJsonToFile(converter.convertLoadedVansToJson(algorithm.getLoadedVansAsObject()));
        System.out.println("Результаты распределения выгружены в файл:");
        System.out.println(jsonFileName);
    }

    private static void processCargoVansFromFile() {
        Scanner console = new Scanner(System.in);
        System.out.println("Введите путь к файлу с загруженными фургонами:");
        String filePath = console.nextLine();
        CargoVanToJsonConverter converter = new CargoVanToJsonConverter();
        CargoVanList cargoVanList = converter.getLoadedVansFromJsonFile(filePath);
        System.out.println("Количество обнаруженных в файле фургонов: " + cargoVanList.getCargoVans().size());
        System.out.println("Распределение посылок:");
        cargoVanList.printCargoVanList();
        System.out.println("Общий список посылок из файла:");
        cargoVanList.printAllCargoItems();
        System.out.println("Общее количество посылок из файла: " + cargoVanList.getAllCargoItemsFromVans().size());
    }
}