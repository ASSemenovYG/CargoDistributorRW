package ru.liga.CargoDistributor.cargo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.liga.CargoDistributor.algorithm.DistributionAlgorithm;
import ru.liga.CargoDistributor.algorithm.SingleSortedCargoDistribution;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class CargoVanToJsonConverterTest {

    private final List<CargoItem> cargoItemsToLoad = new ArrayList<>(Arrays.asList(
            new CargoItem(9, 3, 3),
            new CargoItem(6, 3, 2),
            new CargoItem(6, 2, 3),
            new CargoItem(1, 1, 1),
            new CargoItem(5, 1, 5),
            new CargoItem(4, 1, 4),
            new CargoItem(4, 2, 2)
    ));

    private final CargoList cargoList = new CargoListMock(cargoItemsToLoad);

    private final CargoVanToJsonConverter converter = new CargoVanToJsonConverter(true);

    @AfterAll
    static void clearJsonVanTestDirectory() {
        File folder = new File(CargoVanToJsonConverter.TEST_DIRECTORY_TO_WRITE_JSON_FILE);
        File[] fList = folder.listFiles();
        for (File file : fList) {
            String pes = file.getName();
            if (pes.endsWith(CargoVanToJsonConverter.JSON_FILE_EXTENSION)) {
                new File(String.valueOf(file)).delete();
            }
        }
    }

    @Test
    void readJsonFromFile() {
        List<CargoVan> loadedVansFromFile = converter.getLoadedVansFromJsonFile("src/test/resources/test_loaded_vans.json").getCargoVans();
        assertThat(loadedVansFromFile).hasSize(3);
    }

    @Test
    void writeJsonToFile() {
        DistributionAlgorithm singleSortedCargoDistribution = new SingleSortedCargoDistribution(cargoList);
        String resultJson = converter.convertLoadedVansToJson(singleSortedCargoDistribution.getLoadedVansAsObject());
        File jsonVansDirectory = new File(CargoVanToJsonConverter.TEST_DIRECTORY_TO_WRITE_JSON_FILE);
        int numberOfFilesBeforeWriting = Objects.requireNonNull(jsonVansDirectory.list()).length;
        converter.writeJsonToFile(resultJson);
        int numberOfFilesAfterWriting = Objects.requireNonNull(jsonVansDirectory.list()).length;

        assertThat(numberOfFilesAfterWriting).isEqualTo(numberOfFilesBeforeWriting + 1);
    }

    @Test
    void convertLoadedVansToJson() {
        DistributionAlgorithm singleSortedCargoDistribution = new SingleSortedCargoDistribution(cargoList);
        String resultJson = converter.convertLoadedVansToJson(singleSortedCargoDistribution.getLoadedVansAsObject());
        System.out.println(resultJson);

        assertThat(resultJson)
                .contains("\"length\":1,\"width\":5,\"size\":5,\"name\":\"55555\"")
                .contains("\"length\":1,\"width\":4,\"size\":4,\"name\":\"4444\"")
                .contains("\"length\":2,\"width\":3,\"size\":6,\"name\":\"666\\n666\"")
                .contains("\"length\":3,\"width\":3,\"size\":9,\"name\":\"999\\n999\\n999\"")
                .contains("\"length\":2,\"width\":2,\"size\":4,\"name\":\"44\\n44\"")
                .contains("\"length\":3,\"width\":2,\"size\":6,\"name\":\"66\\n66\\n66\"");
    }
}
