package ru.liga.CargoDistributor.cargo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CargoListMockTest {
    private CargoList testNullList;
    private CargoList testEmptyList;

    @BeforeEach
    public void setup() {
        testNullList = new CargoListMock(null);
        testEmptyList = new CargoListMock(new ArrayList<>());
    }

    @Test
    void getCargo_null() {
        assertNull(testNullList.getCargo());
    }

    @Test
    void isEmpty_nullCargoValid() {
        assertTrue(testNullList.isEmptyOrNull());
    }

    @Test
    void isEmpty_emptyListValid() {
        assertTrue(testEmptyList.isEmptyOrNull());
        assertThat(testEmptyList.getCargo())
                .hasSize(0);
    }

    @Test
    void createCargoListWithOneItem() {
        CargoList cargoList = new CargoListMock(List.of(new CargoItem(4, 2, 2)));
        assertFalse(cargoList.isEmptyOrNull());
        assertThat(cargoList.getCargo())
                .hasSize(1);
        cargoList.printCargoItems();
    }
}