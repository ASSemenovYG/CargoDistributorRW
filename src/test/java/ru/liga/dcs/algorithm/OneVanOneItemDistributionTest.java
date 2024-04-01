package ru.liga.dcs.algorithm;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.liga.dcs.cargo.CargoItem;
import ru.liga.dcs.cargo.CargoList;
import ru.liga.dcs.cargo.CargoListMock;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class OneVanOneItemDistributionTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    private static final String PRINTED_VAN_666 =
            """
                    +      +\r
                    +      +\r
                    +      +\r
                    +      +\r
                    +      +\r
                    +666   +\r
                    ++++++++\r
                    """;

    private static final String PRINTED_VAN_999 =
            """
                    +      +\r
                    +      +\r
                    +      +\r
                    +      +\r
                    +      +\r
                    +999   +\r
                    ++++++++\r
                    """;
    private static final String PRINTED_VAN_1 =
            """
                    +      +\r
                    +      +\r
                    +      +\r
                    +      +\r
                    +      +\r
                    +1     +\r
                    ++++++++\r
                    """;
    private static final String PRINTED_VAN_55555 =
            """
                    +      +\r
                    +      +\r
                    +      +\r
                    +      +\r
                    +      +\r
                    +55555 +\r
                    ++++++++\r
                    """;

    private static final String PRINTED_VAN_4444 =
            """
                    +      +\r
                    +      +\r
                    +      +\r
                    +      +\r
                    +      +\r
                    +4444  +\r
                    ++++++++\r
                    """;

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
    void printLoadedVans() {
        CargoList cargoList = new CargoListMock(Arrays.asList(
                new CargoItem("999"),
                new CargoItem("666"),
                new CargoItem("1"),
                new CargoItem("55555"),
                new CargoItem("4444")
        ));
        DistributionAlgorithm oneVanOneItem = new OneVanOneItemDistribution(cargoList);
        oneVanOneItem.printLoadedVans();

        assertThat(outContent.toString()).contains(PRINTED_VAN_999);
        assertThat(outContent.toString()).contains(PRINTED_VAN_666);
        assertThat(outContent.toString()).contains(PRINTED_VAN_1);
        assertThat(outContent.toString()).contains(PRINTED_VAN_4444);
        assertThat(outContent.toString()).contains(PRINTED_VAN_55555);
    }
}