package ru.liga.cargodistributor.controller;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.liga.cargodistributor.algorithm.*;
import ru.liga.cargodistributor.algorithm.DistributionAlgorithmService;
import ru.liga.cargodistributor.algorithm.SingleSortedCargoDistributionAlgorithmService;
import ru.liga.cargodistributor.cargo.CargoConverterService;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.CargoVanList;
import ru.liga.cargodistributor.util.FileService;

@ShellComponent
public class CargoDistributorController {
    @ShellMethod("Distribute cargo from file")
    public void distribute(
            @ShellOption(
                    help = "Путь к файлу с посылками",
                    value = {"-p"}
            ) String filePath,
            @ShellOption(
                    help = "Код алгоритма:\n1 : OneVanOneItemDistribution\n2 : SingleSortedCargoDistribution\n3 : SimpleFitDistribution",
                    value = {"-c"}
            ) int algorithmCode,
            @ShellOption(
                    help = "Максимальное количество фургонов, доступное для распределения",
                    value = {"-l"}
            ) int vanLimit
    ) {
        FileService fileService = new FileService();
        CargoConverterService cargoConverterService = new CargoConverterService();
        CargoItemList cargoList = new CargoItemList(
                cargoConverterService.parseCargoItems(
                        fileService.readFromFileByPath(filePath)
                )
        );
        if (cargoList.isEmptyOrNull()) {
            System.out.println("В файле не найдено ни одной посылки!");
            return;
        }
        System.out.println("В файле найдены следующие посылки:");
        System.out.println(cargoList.getCargoItemNamesAsString());

        DistributionAlgorithmService algorithm = switch (algorithmCode) {
            case 1 -> new OneVanOneItemDistributionAlgorithmService();
            case 2 -> new SingleSortedCargoDistributionAlgorithmService();
            case 3 -> new SimpleFitDistributionAlgorithmService();
            default -> throw new IllegalArgumentException("Введен неверный код алгоритма");
        };

        CargoVanList cargoVanList = new CargoVanList();
        cargoVanList.distributeCargo(algorithm, cargoList);

        if (!cargoVanList.isListSizeLessOrEqualThanMaxSize(vanLimit)) {
            System.out.println("Не удалось распределить посылки из файла по указанному количеству фургонов");
            return;
        }

        System.out.println("Результат распределения посылок по грузовым фургонам:");
        System.out.println(cargoVanList.getCargoVanListAsString(cargoConverterService));

        String jsonFileName = fileService.writeStringToFile(cargoConverterService.serializeLoadedVansToJson(cargoVanList));
        System.out.println("Результаты распределения выгружены в файл:");
        System.out.println(jsonFileName);
    }

    @ShellMethod("Read loaded cargo vans from file")
    public void readcargo(
            @ShellOption(
                    help = "Путь к файлу с фургонами",
                    value = {"-p"}
            ) String filePath) {
        FileService fileService = new FileService();
        CargoConverterService cargoConverterService = new CargoConverterService();
        CargoVanList cargoVanList = cargoConverterService.deserializeLoadedVansFromJson(fileService.readFromFileByPath(filePath));
        System.out.println("Количество обнаруженных в файле фургонов: " + cargoVanList.getCargoVans().size());
        System.out.println("Распределение посылок:");
        System.out.println(cargoVanList.getCargoVanListAsString(cargoConverterService));
        System.out.println("Общий список посылок из файла:");
        System.out.println(cargoVanList.getAllCargoItemNamesAsString());
        System.out.println("Общее количество посылок из файла: " + cargoVanList.getAllCargoItemsFromVans().size());
    }
}