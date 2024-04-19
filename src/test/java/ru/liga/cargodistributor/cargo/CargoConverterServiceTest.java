package ru.liga.cargodistributor.cargo;

import org.junit.jupiter.api.Test;
import ru.liga.cargodistributor.algorithm.DistributionAlgorithmService;
import ru.liga.cargodistributor.algorithm.SingleSortedCargoDistributionAlgorithmService;
import ru.liga.cargodistributor.util.FileService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CargoConverterServiceTest {
    private final List<CargoItem> cargoItemsToLoad = new ArrayList<>(Arrays.asList(
            new CargoItem(9, 3, 3),
            new CargoItem(6, 3, 2),
            new CargoItem(6, 2, 3),
            new CargoItem(1, 1, 1),
            new CargoItem(5, 1, 5),
            new CargoItem(4, 1, 4),
            new CargoItem(4, 2, 2)
    ));

    private final CargoItemList cargoList = new CargoItemList(cargoItemsToLoad);
    private final FileService fileService = new FileService(true);
    private final CargoConverterService cargoConverterService = new CargoConverterService();

    @Test
    void deserializeLoadedVansFromJson() {
        List<CargoVan> loadedVansFromFile = cargoConverterService.deserializeLoadedVansFromJson(fileService.readFromFileByPath("src/test/resources/test_loaded_vans.json")).getCargoVans();
        assertThat(loadedVansFromFile).hasSize(3);
    }

    @Test
    void serializeLoadedVansToJson() {
        DistributionAlgorithmService singleSortedCargoDistribution = new SingleSortedCargoDistributionAlgorithmService();
        CargoVanList cargoVanList = new CargoVanList();
        cargoVanList.distributeCargo(singleSortedCargoDistribution, cargoList);

        String resultJson = cargoConverterService.serializeLoadedVansToJson(cargoVanList);
        System.out.println(resultJson);

        assertThat(resultJson)
                .contains("\"length\":1,\"width\":5,\"size\":5,\"name\":\"55555\"")
                .contains("\"length\":1,\"width\":4,\"size\":4,\"name\":\"4444\"")
                .contains("\"length\":2,\"width\":3,\"size\":6,\"name\":\"666\\n666\"")
                .contains("\"length\":3,\"width\":3,\"size\":9,\"name\":\"999\\n999\\n999\"")
                .contains("\"length\":2,\"width\":2,\"size\":4,\"name\":\"44\\n44\"")
                .contains("\"length\":3,\"width\":2,\"size\":6,\"name\":\"66\\n66\\n66\"");
    }

    @Test
    void parseCargoItems() {
        List<CargoItem> cargoItemsToCompare = new ArrayList<>(Arrays.asList(
                new CargoItem(9, 3, 3),
                new CargoItem(6, 2, 3),
                new CargoItem(1, 1, 1),
                new CargoItem(5, 1, 5),
                new CargoItem(1, 1, 1),
                new CargoItem(3, 1, 3)
        ));
        List<CargoItem> cargoItems = cargoConverterService.parseCargoItems(fileService.readFromFileByPath("src/test/resources/test_valid_cargo_file.pkg"));

        assertThat(cargoItems).containsExactlyInAnyOrderElementsOf(cargoItemsToCompare);

    }

    @Test
    void convertCargoVanListToString_oneVan() {
        CargoItem cargoItem = new CargoItem(9, 3, 3);
        CargoVan van = new CargoVan(cargoItem);
        List<CargoVan> cargoVans = new ArrayList<>();
        cargoVans.add(van);
        String result = cargoConverterService.convertCargoVanListToString(new CargoVanList(cargoVans));

        assertThat(result).isEqualTo("""
                
                +      +
                +      +
                +      +
                +999   +
                +999   +
                +999   +
                ++++++++
                """);
    }
}