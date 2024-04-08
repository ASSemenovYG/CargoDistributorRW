package ru.liga.dcs.cargo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;

/**
 * Класс содержащий методы сериализации/десериализации списка грузовых фургонов и записи/вычитки их из файла
 */
public class CargoVanToJsonConverter {
    private static final Logger LOGGER = LogManager.getLogger(CargoVanToJsonConverter.class);
    public static final String DIRECTORY_TO_WRITE_JSON_FILE = "src/main/resources/json_vans";
    public static final String TEST_DIRECTORY_TO_WRITE_JSON_FILE = "src/test/resources/json_vans_test";
    public static final String JSON_FILE_EXTENSION = ".json";
    private final boolean isTest;

    public CargoVanToJsonConverter() {
        this(false);
    }

    public CargoVanToJsonConverter(boolean isTest) {
        this.isTest = isTest;
    }

    public void writeJsonToFile(String json) {
        String jsonFileName = getNextJsonFileName();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFileName))) {
            LOGGER.trace("Сохранение json в файл: " + jsonFileName + "\nJSON:\n" + json);
            writer.write(json);
        } catch (IOException e) {
            LOGGER.error("writeJsonToFile:");
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public CargoVanList getLoadedVansFromJsonFile(String filePath) {
        StringReader reader = new StringReader(readJsonFromFile(filePath));
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            return mapper.readValue(reader, CargoVanList.class);
        } catch (IOException e) {
            LOGGER.error("getLoadedVansFromJsonFile:");
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    String convertLoadedVansToJson(CargoVanList loadedVans) {
        LOGGER.info("Конвертация списка грузовиков в json");
        try (StringWriter writer = new StringWriter()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(writer, loadedVans);
            LOGGER.trace("Сконвертированный список грузовиков в json:\n" + writer);
            return writer.toString();
        } catch (IOException e) {
            LOGGER.error("convertLoadedVansToJson:");
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    String readJsonFromFile(String filePath) {
        LOGGER.info("Чтение json со списком грузовиков из файла " + filePath);
        StringBuilder resultJson = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                LOGGER.trace("Reading line: " + line);
                resultJson.append(line);
            }
        } catch (IOException e) {
            LOGGER.error("readJsonFromFile:");
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
        LOGGER.trace("Result json:\n" + resultJson);
        return resultJson.toString();
    }

    private String getNextJsonFileName() {
        return "%s/%s_%s%s".formatted(
                (isTest) ? TEST_DIRECTORY_TO_WRITE_JSON_FILE : DIRECTORY_TO_WRITE_JSON_FILE,
                Thread.currentThread().getName(),
                new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date()),
                JSON_FILE_EXTENSION
        );
    }
}
