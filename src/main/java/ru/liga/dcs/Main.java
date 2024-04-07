package ru.liga.dcs;

import ru.liga.dcs.algorithm.DistributionAlgorithm;
import ru.liga.dcs.algorithm.OneVanOneItemDistribution;
import ru.liga.dcs.algorithm.SingleSortedCargoDistribution;
import ru.liga.dcs.cargo.CargoList;
import ru.liga.dcs.cargo.CargoListFromFile;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        /*
        TODO:   1. Прикрутить логирование в файл. Залогировать важные бизнес параметры и ошибок. Slf4j+log4j2
                2. Придумать свою структуру хранения файлов с машинами в формате json, сохранить как .json на диск.
                3. Поддержать ещё одну функцию - теперь на может идти вход загруженную машину в формате .json  и сказать сколько в ней каких посылок.
                4. Передавать на вход ещё и имя алгоритма, по которому будет осуществляться упаковка. Реализовать как минимум 2 алгоритма
                5. На вход передается количество машин, если не удаётся никак погрузить выдаётся ошибка.
         */
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
        //System.out.print("3 : Максимальная оптимизация кузова фургона (default) \n");
        int algorithmCode = console.nextInt();
        DistributionAlgorithm algorithm = switch (algorithmCode) {
            case 1 -> new OneVanOneItemDistribution(cargoList);
            default -> new SingleSortedCargoDistribution(cargoList);
            //default -> new MaximumCapacityDistribution(cargoList);
        };
        System.out.print("Результат распределения посылок по грузовым фургонам: \n");
        algorithm.printLoadedVans();
    }
}