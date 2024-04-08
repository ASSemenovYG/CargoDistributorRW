package ru.liga.dcs.cargo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.text.SimpleDateFormat;

/**
 * Класс содержащий методы сериализации/десериализации списка грузовых фургонов и записи/вычитки их из файла
 */
public class CargoVanToJsonConverter {
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getNextJsonFileName()))) {
            writer.write(json);
        } catch (IOException e) {
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
            throw new RuntimeException(e);
        }
    }

    String convertLoadedVansToJson(CargoVanList loadedVans) {
        try (StringWriter writer = new StringWriter()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(writer, loadedVans);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    String readJsonFromFile(String filePath) {
        StringBuilder resultJson = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultJson.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
