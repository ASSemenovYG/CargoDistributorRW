package ru.liga.dcs.cargoList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CargoListFromFileTest {

    @Test
    void createCargoListWrongFilePath() {
        Assertions.assertThrows(RuntimeException.class, () ->
        {
            new CargoListFromFile("C:\\Users\\ArtemSemenov\\Desktop\\work\\prodcat_export\\fake111.txt");
        });
    }

    @Test
    void createCargoListFromValidFile() {
        CargoList cargoList = new CargoListFromFile("src/test/java/ru/liga/dcs/cargoList/testValidCargoFile");
        List<String> cargo = cargoList.getCargo();
        assertThat(cargo)
                .hasSize(9)
                .containsExactlyInAnyOrderElementsOf(Arrays.asList("333","999","999","999","666","666","55555","1","1"));
    }

}