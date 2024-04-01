package ru.liga.dcs.cargo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CargoVanTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void printVanLines() {
        String systemOut =
                """
                        +      +\r
                        +      +\r
                        +      +\r
                        +      +\r
                        +      +\r
                        +999   +\r
                        ++++++++\r
                        """;
        CargoVan van = new CargoVan();
        CargoVan.CargoVanLine line = new CargoVan.CargoVanLine();
        line.addCargoItem(new CargoItem("999"), 0);
        van.addLine(line);
        van.printVanLines();

        assertEquals(systemOut, outContent.toString());
    }
}