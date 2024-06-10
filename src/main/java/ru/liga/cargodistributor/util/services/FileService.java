package ru.liga.cargodistributor.util.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.liga.cargodistributor.util.exceptions.FromMultipartFileToFileException;
import ru.liga.cargodistributor.util.exceptions.ReadFromFileException;
import ru.liga.cargodistributor.util.exceptions.WriteToFileException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public static final String DIRECTORY_TO_WRITE_FILE_FROM_MULTIPART = "src/main/resources/fromMultipart";
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
            LOGGER.trace("Сохранение в файл: {}\ncontent:\n{}", fileName, content);
            writer.write(content);
        } catch (IOException e) {
            LOGGER.error("writeStringToFile: {}", e.getMessage());
            throw new WriteToFileException(e.getMessage(), e);
        }
        return fileName;
    }

    public String readFromFileByPath(String filePath) {
        LOGGER.info("Чтение данных из файла по пути {}", filePath);
        try {
            return getFileContent(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            LOGGER.error("readFromFileByPath: {}", e.getMessage());
            throw new ReadFromFileException(e.getMessage(), e);
        }
    }

    public String readFromFile(File file) {
        LOGGER.info("Чтение данных из файла");
        try {
            return getFileContent(new FileReader(file));
        } catch (FileNotFoundException e) {
            LOGGER.error("readFromFile: {}", e.getMessage());
            throw new ReadFromFileException(e.getMessage(), e);
        }
    }

    public File multipartFileToFile (MultipartFile multipart) {
        Path filePath = Paths.get(DIRECTORY_TO_WRITE_FILE_FROM_MULTIPART, multipart.getOriginalFilename());
        try {
            multipart.transferTo(filePath);
        } catch (IOException e) {
            LOGGER.error("multipartFileToFile: {}", e.getMessage());
            throw new FromMultipartFileToFileException(e.getMessage(), e);
        }
        return filePath.toFile();
    }

    private String getFileContent(FileReader fileReader) {
        StringBuilder result = new StringBuilder();
        try (BufferedReader br = new BufferedReader(fileReader)) {
            String line;
            while ((line = br.readLine()) != null) {
                LOGGER.trace("Reading line: {}", line);
                if (!result.isEmpty()) {
                    result.append("\n");
                }
                result.append(line);
            }
        } catch (IOException e) {
            LOGGER.error("getFileContent: {}", e.getMessage());
            throw new ReadFromFileException(e.getMessage(), e);
        }
        LOGGER.trace("Result :\n{}", result);
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
