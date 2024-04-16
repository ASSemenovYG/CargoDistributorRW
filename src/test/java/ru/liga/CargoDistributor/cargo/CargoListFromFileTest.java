package ru.liga.CargoDistributor.cargo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class CargoListFromFileTest {

    @Test
    void createCargoListWrongFilePath() {
        Assertions.assertThrows(RuntimeException.class, () ->
                new CargoListFromFile("C:\\Users\\ArtemSemenov\\Desktop\\work\\prodcat_export\\fake111.txt"));
    }

    @Test
    void createCargoListFromValidFile() {
        CargoList cargoList = new CargoListFromFile("src/test/resources/test_valid_cargo_file.pkg");
        cargoList.getCargo();
        assertThat(cargoList.getCargoItemNames())
                .hasSize(6)
                .containsExactlyInAnyOrderElementsOf(Arrays.asList("333", "999\n999\n999", "666\n666", "55555", "1", "1"));
        cargoList.printCargoItems();
    }

    @Test
    void createCargoListFromValidFile1() {
        CargoList cargoList = new CargoListFromFile("src/test/resources/test_valid_cargo_file_1.pkg");
        cargoList.getCargo();
        assertThat(cargoList.getCargoItemNames())
                .hasSize(7)
                .containsExactlyInAnyOrderElementsOf(Arrays.asList("333","333","333", "999\n999\n999", "666\n666", "55555", "55555"));
        cargoList.printCargoItems();
    }

    @Test
    void createCargoListFromValidFile2() {
        CargoList cargoList = new CargoListFromFile("src/test/resources/test_valid_cargo_file_2.pkg");
        cargoList.getCargo();
        assertThat(cargoList.getCargoItemNames())
                .hasSize(10)
                .containsExactlyInAnyOrderElementsOf(Arrays.asList("1","1","22", "22", "333", "333", "4444", "4444", "8888\n8888", "8888\n8888"));
        cargoList.printCargoItems();
    }

    @Test
    void createCargoListFromValidFile3() {
        CargoList cargoList = new CargoListFromFile("src/test/resources/test_valid_cargo_file_3.pkg");
        cargoList.getCargo();
        assertThat(cargoList.getCargoItemNames())
                .hasSize(16)
                .containsExactlyInAnyOrderElementsOf(Arrays.asList(
                        "1",
                        "1",
                        "22",
                        "22",
                        "2\n2",
                        "333",
                        "333",
                        "3\n3\n3",
                        "4444",
                        "4444",
                        "4\n4\n4\n4",
                        "44\n44",
                        "8888\n8888",
                        "8888\n8888",
                        "88\n88\n88\n88",
                        "999\n999\n999"
                ));
        cargoList.printCargoItems();
    }

    @Test
    void createCargoListFromFileWithInvalidItem() {
        Throwable thrown = catchThrowable(() -> new CargoListFromFile("src/test/resources/test_cargo_file_with_invalid_item.pkg"));
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Во входных данных обнаружена невалидная посылка")
                .hasMessageContaining("Посылки могут быть только прямоугольными");
    }

    @Test
    void createCargoListWithInvalidWidth() {
        List<LinkedList<String>> cargo = new ArrayList<>();
        LinkedList<String> cargoItem = new LinkedList<>(
                List.of("88888888")
        );
        cargo.add(cargoItem);

        Throwable thrown = catchThrowable(() -> new CargoListFromFile(cargo));
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Во входных данных обнаружена невалидная посылка")
                .hasMessageContaining("Ширина посылки")
                .hasMessageContaining("превышает ширину грузового фургона");
    }

    @Test
    void createCargoListWithInvalidLength() {
        List<LinkedList<String>> cargo = new ArrayList<>();
        LinkedList<String> cargoItem = new LinkedList<>(
                List.of("9","9","9","9","9","9","9","9","9")
        );
        cargo.add(cargoItem);

        Throwable thrown = catchThrowable(() -> new CargoListFromFile(cargo));
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Во входных данных обнаружена невалидная посылка")
                .hasMessageContaining("Длина посылки")
                .hasMessageContaining("превышает длину грузового фургона");
    }

    @Test
    void createCargoListWithInvalidSize() {
        List<LinkedList<String>> cargo = new ArrayList<>();
        LinkedList<String> cargoItem = new LinkedList<>(
                List.of("999","999")
        );
        cargo.add(cargoItem);

        Throwable thrown = catchThrowable(() -> new CargoListFromFile(cargo));
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Во входных данных обнаружена невалидная посылка")
                .hasMessageContaining("Некорректные параметры посылки, размер 9 не соответствует длине 2 и ширине 3");
    }

}