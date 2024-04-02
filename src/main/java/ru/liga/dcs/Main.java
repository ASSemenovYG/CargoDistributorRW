package ru.liga.dcs;

import ru.liga.dcs.algorithm.DistributionAlgorithm;
import ru.liga.dcs.algorithm.MaximumCapacityDistribution;
import ru.liga.dcs.algorithm.OneVanOneItemDistribution;
import ru.liga.dcs.algorithm.SingleSortedCargoDistribution;
import ru.liga.dcs.cargo.CargoList;
import ru.liga.dcs.cargo.CargoListFromFile;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
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
        System.out.print("\nПожалуйста, введите желаемый алгоритм распределения: \n");
        System.out.print("1 : Одна посылка - один фургон \n");
        System.out.print("2 : Распределение по одной посылке на паллете друг на друга после сортировки \n");
        System.out.print("3 : Максимальная оптимизация кузова фургона (default) \n");
        int algorithmCode = console.nextInt();
        DistributionAlgorithm algorithm = switch (algorithmCode) {
            case 1 -> new OneVanOneItemDistribution(cargoList);
            case 2 -> new SingleSortedCargoDistribution(cargoList);
            default -> new MaximumCapacityDistribution(cargoList);
        };
        System.out.print("Результат распределения посылок по грузовым фургонам: \n");
        algorithm.printLoadedVans();
    }
}