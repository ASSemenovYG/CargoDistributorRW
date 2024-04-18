package ru.liga.CargoDistributor.cargo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;

/**
 * Класс содержащий методы записи/вычитки из файла
 */
@Service
public class FileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);
    public static final String DIRECTORY_TO_WRITE_JSON_FILE = "src/main/resources/json_vans";
    public static final String TEST_DIRECTORY_TO_WRITE_JSON_FILE = "src/test/resources/json_vans_test";
    public static final String JSON_FILE_EXTENSION = ".json";
    private final boolean isTest;

    public FileService() {
        this(false);
    }

    public FileService(boolean isTest) {
        this.isTest = isTest;
    }

    public String writeStringToFile(String content) {
        String fileName = getNextJsonFileName();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            LOGGER.trace("Сохранение в файл: " + fileName + "\ncontent:\n" + content);
            writer.write(content);
        } catch (IOException e) {
            LOGGER.error("writeStringToFile: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return fileName;
    }

    public String readFromFile(String filePath) {
        LOGGER.info("Чтение данных из файла " + filePath);
        StringBuilder result = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                LOGGER.trace("Reading line: " + line);
                if (!result.isEmpty()) {
                    result.append("\n");
                }
                result.append(line);
            }
        } catch (IOException e) {
            LOGGER.error("readFromFile: " + e.getMessage());
            throw new RuntimeException(e);
        }
        LOGGER.trace("Result :\n" + result);
        return result.toString();
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
