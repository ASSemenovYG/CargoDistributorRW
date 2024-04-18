package ru.liga.CargoDistributor.cargo;

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

    public CargoVanList deserializeLoadedVansFromJson(String content) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            return mapper.readValue(content, CargoVanList.class);
        } catch (IOException e) {
            LOGGER.error("deserializeLoadedVansFromJson: " + e.getMessage());
            //todo: сделать кастомный exception
            throw new RuntimeException(e);
        }
    }

    public String serializeLoadedVansToJson(CargoVanList loadedVans) {
        LOGGER.info("Конвертация списка грузовиков в json");
        try (StringWriter writer = new StringWriter()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(writer, loadedVans);
            LOGGER.trace("Сконвертированный список грузовиков в json:\n" + writer);
            return writer.toString();
        } catch (IOException e) {
            LOGGER.error("serializeLoadedVansToJson: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<CargoItem> parseCargoItems(String content) {
        LOGGER.info("Начинаю чтение списка посылок");
        List<LinkedList<String>> fileLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new StringReader(content))) {
            String line;
            LinkedList<String> unparsedCargoItem = new LinkedList<>();
            while ((line = br.readLine()) != null) {
                LOGGER.trace("Reading line: " + line);
                if (line.isEmpty()) {
                    if (!unparsedCargoItem.isEmpty()) {
                        LOGGER.debug("Обнаружен конец текущей посылки, добавляю текущую посылку в список посылок");
                        fileLines.add(new LinkedList<>(unparsedCargoItem));
                        unparsedCargoItem.clear();
                    }
                } else {
                    LOGGER.debug("Добавляю строку " + line + " к текущей посылке");
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
            LOGGER.error("parseCargoItems: " + e.getMessage());
            throw new RuntimeException(e);
        }

        List<CargoItem> result = new ArrayList<>();
        for (LinkedList<String> unparsedCargoItem : fileLines) {
            result.add(new CargoItem(unparsedCargoItem));
        }
        return result;
    }
}
