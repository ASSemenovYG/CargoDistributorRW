package ru.liga.cargodistributor.cargo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class CargoConverterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CargoConverterService.class);
    private static final String VAN_BORDER_SYMBOL = "+";
    private static final String EMPTY_CARGO_CELL_SYMBOL = " ";
    private static final String VAN_BACK_WALL = VAN_BORDER_SYMBOL.repeat(CargoVan.VAN_WIDTH + 2);

    public CargoVanList deserializeLoadedVansFromJson(String content) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            return mapper.readValue(content, CargoVanList.class);
        } catch (IOException e) {
            LOGGER.error("deserializeLoadedVansFromJson: {}", e.getMessage());
            throw new CargoVanDeserializationException(e.getMessage(), e);
        }
    }

    public String serializeLoadedVansToJson(CargoVanList loadedVans) {
        LOGGER.info("Конвертация списка грузовиков в json");
        try (StringWriter writer = new StringWriter()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(writer, loadedVans);
            LOGGER.trace("Сконвертированный список грузовиков в json:\n{}", writer);
            return writer.toString();
        } catch (IOException e) {
            LOGGER.error("serializeLoadedVansToJson: {}", e.getMessage());
            throw new CargoVanSerializationException(e.getMessage(), e);
        }
    }

    public List<CargoItem> parseCargoItems(String content) {
        LOGGER.info("Начинаю чтение списка посылок");
        List<LinkedList<String>> fileLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new StringReader(content))) {
            String line;
            LinkedList<String> unparsedCargoItem = new LinkedList<>();
            while ((line = br.readLine()) != null) {
                LOGGER.trace("Reading line: {}", line);
                if (line.isEmpty()) {
                    if (!unparsedCargoItem.isEmpty()) {
                        LOGGER.debug("Обнаружен конец текущей посылки, добавляю текущую посылку в список посылок");
                        fileLines.add(new LinkedList<>(unparsedCargoItem));
                        unparsedCargoItem.clear();
                    }
                } else {
                    LOGGER.debug("Добавляю строку {} к текущей посылке", line);
                    unparsedCargoItem.add(line);
                }
            }
            LOGGER.info("Чтение списка посылок завершено");
            if (!unparsedCargoItem.isEmpty()) {
                LOGGER.debug("Добавляю крайнюю посылку в список посылок");
                fileLines.add(new LinkedList<>(unparsedCargoItem));
                unparsedCargoItem.clear();
            }
        } catch (IOException e) {
            LOGGER.error("parseCargoItems: {}", e.getMessage());
            throw new CargoItemParserException(e.getMessage(), e);
        }

        List<CargoItem> result = new ArrayList<>();
        for (LinkedList<String> unparsedCargoItem : fileLines) {
            result.add(new CargoItem(unparsedCargoItem));
        }
        return result;
    }

    /**
     * @return String с кузовами грузовиков в формате:
     *
     * <br>+8888  +
     * <br>+8888  +
     * <br>+118888+
     * <br>+224444+
     * <br>+224444+
     * <br>+333333+
     * <br>++++++++
     */
    protected String convertCargoVanListToString(CargoVanList cargoVanList) {
        StringBuilder sb = new StringBuilder();
        for (CargoVan cargoVan : cargoVanList.getCargoVans()) {
            sb.append("\n").append(convertVanCargoToString(cargoVan)).append("\n");
        }
        return sb.toString();
    }

    private String convertVanCargoToString(CargoVan cargoVan) {
        StringBuilder sb = new StringBuilder();
        for (int i = CargoVan.VAN_LENGTH - 1; i >= 0; i--) {
            if (i < CargoVan.VAN_LENGTH - 1) {
                sb.append("\n");
            }
            sb.append(VAN_BORDER_SYMBOL);
            for (int j = 0; j < CargoVan.VAN_WIDTH; j++) {
                sb.append((cargoVan.getCargo()[i][j].isEmpty()) ? EMPTY_CARGO_CELL_SYMBOL : cargoVan.getCargo()[i][j].getCellItemTitle());
            }
            sb.append(VAN_BORDER_SYMBOL);
        }
        sb.append("\n").append(VAN_BACK_WALL);
        LOGGER.trace("Returning to print cargo van:\n{}", sb);
        return sb.toString();
    }
}