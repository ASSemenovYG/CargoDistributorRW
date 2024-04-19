package ru.liga.cargodistributor.cargo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.liga.cargodistributor.algorithm.DistributionAlgorithmService;
import ru.liga.cargodistributor.algorithm.SingleSortedCargoDistributionAlgorithmService;
import ru.liga.cargodistributor.util.FileService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class FileServiceTest {
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

    @AfterAll
    static void clearJsonVanTestDirectory() {
        File folder = new File(FileService.TEST_DIRECTORY_TO_WRITE_JSON_FILE);
        File[] fList = folder.listFiles();
        for (File file : fList) {
            String pes = file.getName();
            if (pes.endsWith(FileService.JSON_FILE_EXTENSION)) {
                new File(String.valueOf(file)).delete();
            }
        }
    }

    @Test
    void readJsonFromFile() {
        String json = fileService.readFromFileByPath("src/test/resources/test_hello_world.json");
        assertThat(json).isEqualTo("{\"Hello\":\"world\"}");
    }

    @Test
    void writeJsonToFile() {
        DistributionAlgorithmService singleSortedCargoDistribution = new SingleSortedCargoDistributionAlgorithmService();
        CargoVanList cargoVanList = new CargoVanList();
        cargoVanList.distributeCargo(singleSortedCargoDistribution, cargoList);

        String resultJson = cargoConverterService.serializeLoadedVansToJson(cargoVanList);
        File jsonVansDirectory = new File(FileService.TEST_DIRECTORY_TO_WRITE_JSON_FILE);
        int numberOfFilesBeforeWriting = Objects.requireNonNull(jsonVansDirectory.list()).length;
        fileService.writeStringToFile(resultJson);
        int numberOfFilesAfterWriting = Objects.requireNonNull(jsonVansDirectory.list()).length;

        assertThat(numberOfFilesAfterWriting).isEqualTo(numberOfFilesBeforeWriting + 1);
    }
}
