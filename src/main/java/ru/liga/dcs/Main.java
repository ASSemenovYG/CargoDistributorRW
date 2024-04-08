package ru.liga.dcs;

import ru.liga.dcs.algorithm.DistributionAlgorithm;
import ru.liga.dcs.algorithm.OneVanOneItemDistribution;
import ru.liga.dcs.algorithm.SingleSortedCargoDistribution;
import ru.liga.dcs.cargo.CargoList;
import ru.liga.dcs.cargo.CargoListFromFile;
import ru.liga.dcs.cargo.CargoVanList;
import ru.liga.dcs.cargo.CargoVanToJsonConverter;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        System.out.print("\nПожалуйста, введите число, чтобы выбрать желаемую функцию: \n");
        System.out.print("1 : Чтение списка загруженных фургонов из файла \n");
        System.out.print("2 : Чтение списка посылок из файла и распределение по грузовым фургонам (default) \n");
        int functionCode = console.nextInt();
        switch (functionCode) {
            case 1 -> processCargoVansFromFile();
            default -> processCargoFromFile();
        }
    }

    private static void processCargoFromFile() {
        Scanner console = new Scanner(System.in);
        System.out.print("Введите путь к файлу с посылками: \n");
        String filePath = console.nextLine();
        CargoList cargoList = new CargoListFromFile(filePath);

        if (cargoList.isEmptyOrNull()) {
            System.out.print("В файле не найдено ни одной посылки! \n");
            return;
        }
        System.out.print("В файле найдены следующие посылки: \n");
        System.out.print(cargoList.getCargoItemNames().toString());
        System.out.print("\nПожалуйста, введите число, чтобы выбрать желаемый алгоритм распределения: \n");
        System.out.print("1 : OneVanOneItemDistribution \n");
        System.out.print("2 : SingleSortedCargoDistribution \n");
        //System.out.print("3 : Максимальная оптимизация кузова фургона (default) \n");
        int algorithmCode = console.nextInt();
        System.out.print("Пожалуйста, укажите максимальное количество грузовых фургонов для распределения: \n");
        int maxCargoVanCount = console.nextInt();

        DistributionAlgorithm algorithm = switch (algorithmCode) {
            case 1 -> new OneVanOneItemDistribution(cargoList);
            default -> new SingleSortedCargoDistribution(cargoList);
            //default -> new MaximumCapacityDistribution(cargoList);
        };

        algorithm.checkIfLoadedVansCountLessThanMaxCount(maxCargoVanCount);
        System.out.print("Результат распределения посылок по грузовым фургонам: \n");
        algorithm.printLoadedVans();
    }

    private static void processCargoVansFromFile() {
        Scanner console = new Scanner(System.in);
        System.out.print("Введите путь к файлу с посылками: \n");
        String filePath = console.nextLine();
        CargoVanToJsonConverter converter = new CargoVanToJsonConverter();
        CargoVanList cargoVanList = converter.getLoadedVansFromJsonFile(filePath);

        System.out.print("Количество обнаруженных в файле фургонов: " + cargoVanList.getCargoVans().size() + "\n");
        System.out.print("Распределение посылок: \n");
        cargoVanList.printCargoVanList();
        System.out.print("\nОбщий список посылок из файла: \n");
        System.out.println(cargoVanList.getAllCargoItemNames());
        System.out.print("\nОбщее количество посылок из файла: " + cargoVanList.getAllCargoItemsFromVans().size() + "\n");
    }
}