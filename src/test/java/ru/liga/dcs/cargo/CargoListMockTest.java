package ru.liga.dcs.cargo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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
    void isEmpty_NullPointerException() {
        assertThrows(NullPointerException.class, () ->
        {
            testNullList.isEmpty();
        });
    }

    @Test
    void isEmpty_EmptyList() {
        assertTrue(testEmptyList.isEmpty());
        assertThat(testEmptyList.getCargo())
                .hasSize(0);
    }
}