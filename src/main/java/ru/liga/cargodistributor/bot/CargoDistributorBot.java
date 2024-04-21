package ru.liga.cargodistributor.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.algorithm.DistributionAlgorithmService;
import ru.liga.cargodistributor.algorithm.OneVanOneItemDistributionAlgorithmService;
import ru.liga.cargodistributor.algorithm.SimpleFitDistributionAlgorithmService;
import ru.liga.cargodistributor.algorithm.SingleSortedCargoDistributionAlgorithmService;
import ru.liga.cargodistributor.cargo.CargoConverterService;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.CargoVanList;
import ru.liga.cargodistributor.util.FileService;

import java.io.File;

@Component
public class CargoDistributorBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CargoDistributorBot.class);
    private static final String RESULT_JSON_FILE_NAME = "loadedVans.json";
    private static final String SRC_RESOURCES_PATH = "src/main/resources/";
    private static final String CARGO_FILE_EXAMPLE_NAME = "cargo_file_example.pkg";
    private static final String LOADED_VANS_FILE_EXAMPLE_NAME = "loadedVansExample.json";
    private static final String CARGO_FILE_EXAMPLE_PATH = SRC_RESOURCES_PATH + CARGO_FILE_EXAMPLE_NAME;
    private static final String LOADED_VANS_FILE_EXAMPLE_PATH = SRC_RESOURCES_PATH + LOADED_VANS_FILE_EXAMPLE_NAME;
    private static final String HELP_COMMAND_STICKER_ID = "CAACAgIAAxkBAAEL9bxmJFPTU9efBMHORW2P0MoLt4KSWQACWwIAAvNWPxdFcg4Bd_Sh0zQE";
    private static final String ABOUT_COMMAND_VIDEO_ID = "BAACAgIAAxkBAAPnZiRfZOTOCEhcoD205Iz6fDSiTbMAAqZTAALkTSBJd9KpquJL27A0BA";
    private static final String ABOUT_COMMAND_STICKER_ID_1 = "CAACAgIAAxkBAAEL9b5mJFsmbk4g6TvaIYybFKi2wDtuNQACSSwAAnodcUrY0E6TPhUOwjQE";
    private static final String ABOUT_COMMAND_STICKER_ID_2 = "CAACAgIAAxkBAAEL9cBmJFsw7zOK8bp2_y8wmFE1DM-3WAACMC8AAnXKaEp_pe78p-vsCzQE";
    private static final String ABOUT_COMMAND_STICKER_ID_3 = "CAACAgIAAxkBAAEL9cJmJFs8iNpo14WkcPqPNVfFtK3-CQACqi0AAmTJaUqOKPZ9kj4LQjQE";

    private final String token;

    private final TelegramClient telegramClient;
    private final CargoDistributorBotService botService;
    private final CargoConverterService cargoConverterService;
    private final FileService fileService;

    public CargoDistributorBot(@Value("${bot.token}") String token, @Value("${cache.capacity}") int cacheCapacity) {
        this.token = token;
        this.telegramClient = new OkHttpTelegramClient(getBotToken());
        this.botService = new CargoDistributorBotService(cacheCapacity);
        this.cargoConverterService = new CargoConverterService();
        this.fileService = new FileService();
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        //todo: запихнуть все возможные тексты команд в enum
        if (updateHasMessageText(update) && messageText.equals("/start")) {
            processStartCommand(chatId);
        } else if (updateHasMessageText(update) &&
                (messageText.equals("Прочитать посылки из файла и разложить по фургонам") || messageText.equals("/distribute"))
        ) {
            processDistributeCommand(chatId);
        } else if (updateHasMessageText(update) &&
                (messageText.equals("Прочитать json с загруженными фургонами") || messageText.equals("/readcargo"))
        ) {
            processReadCargoCommand(chatId);
        } else if (!updateHasMessageText(update) && update.getMessage().hasDocument() && isLastSendMessageEqualTo(chatId, "Отправь мне файл с посылками")) {
            processCargoListCommand(chatId, update);
        } else if (updateHasMessageText(update) && isLastSendMessageEqualTo(chatId, "Введи максимальное количество грузовых фургонов для распределения")) {
            processReadVanLimitCommand(chatId, messageText);
        } else if (updateHasMessageText(update) && isLastSendMessageEqualTo(chatId, "Выбери алгоритм распределения")) {
            processPickAlgorithmCommand(chatId, messageText);
        } else if ((updateHasMessageText(update) || update.getMessage().hasDocument()) && isLastSendMessageEqualTo(chatId, "Отправь мне файл с загруженными фургонами или скинь json в сообщении")) {
            processReadCargoVansCommand(chatId, update);
        } else if (updateHasMessageText(update) && messageText.equals("/help")) {
            processHelpCommand(chatId);
        } else if (updateHasMessageText(update) && messageText.equals("/about")) {
            processAboutCommand(chatId);
        } else {
            //todo: дефолтный ответ
        }
    }

    private void processStartCommand(long chatId) {
        sendMessage(botService.buildTextMessageWithKeyboard(chatId, "Вот что я могу:", CargoDistributorBotKeyboard.START));
    }

    private void processDistributeCommand(long chatId) {
        sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Отправь мне файл с посылками"));
    }

    private void processReadCargoCommand(long chatId) {
        sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Отправь мне файл с загруженными фургонами или скинь json в сообщении"));
    }

    private void processCargoListCommand(long chatId, Update update) {
        CargoItemList cargoList;
        try {
            cargoList = new CargoItemList(
                    cargoConverterService.parseCargoItems(
                            fileService.readFromFile(
                                    botService.getFileFromUpdate(update, telegramClient)
                            )
                    )
            );
        } catch (RuntimeException e) {
            LOGGER.error("processCargoListCommand: {}", e.getMessage());
            sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Произошла ошибка при обработке файла с посылками:"));
            sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "```" + e.getMessage() + "```"));
            sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Попробуй еще раз"));
            sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Отправь мне файл с посылками"));
            return;
        }

        if (cargoList.isEmptyOrNull()) {
            sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "В файле не найдено ни одной посылки!"));
            returnToStart(chatId);
            return;
        }
        sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "В файле найдены следующие посылки:"));
        sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "```" + cargoList.getCargoItemNamesAsString() + "```"));
        botService.putCargoItemListToCache(String.valueOf(chatId), cargoList);

        sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Введи максимальное количество грузовых фургонов для распределения"));
    }

    private void processReadVanLimitCommand(long chat_id, String message_text) {
        int vanLimit = 0;
        try {
            vanLimit = Integer.parseInt(message_text);
        } catch (NumberFormatException e) {
            LOGGER.error("processReadVanLimitCommand: {}", e.getMessage());
            sendMessage(botService.buildTextMessageWithoutKeyboard(chat_id, "Не могу распознать число. Необходимо ввести целое число"));
            sendMessage(botService.buildTextMessageWithoutKeyboard(chat_id, "Введи максимальное количество грузовых фургонов для распределения"));
            return;
        }
        botService.putVanLimitToCache(String.valueOf(chat_id), vanLimit);
        sendMessage(botService.buildTextMessageWithKeyboard(chat_id, "Выбери алгоритм распределения", CargoDistributorBotKeyboard.PICK_ALGORITHM));
    }

    private void processPickAlgorithmCommand(long chatId, String messageText) {
        DistributionAlgorithmService algorithm;
        try {
            algorithm = switch (messageText) {
                case "OneVanOneItemDistribution" -> new OneVanOneItemDistributionAlgorithmService();
                case "SingleSortedCargoDistribution" -> new SingleSortedCargoDistributionAlgorithmService();
                case "SimpleFitDistribution" -> new SimpleFitDistributionAlgorithmService();
                default -> throw new IllegalArgumentException("Введен неверный код алгоритма");
            };
        } catch (IllegalArgumentException e) {
            LOGGER.error("processPickAlgorithmCommand: couldn't resolve algorithm: {}", e.getMessage());
            sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Я не понял, какой алгоритм ты выбрал"));
            sendMessage(botService.buildTextMessageWithKeyboard(chatId, "Выбери алгоритм распределения", CargoDistributorBotKeyboard.PICK_ALGORITHM));
            return;
        }

        CargoItemList cargoItemList = botService.getCargoItemListFromCache(String.valueOf(chatId));

        if (cargoItemList.isEmptyOrNull()) {
            sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Я не нашел твой список посылок, который ты отправлял до этого"));
            sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Отправь мне файл с посылками"));
            return;
        }

        CargoVanList cargoVanList = new CargoVanList();
        cargoVanList.distributeCargo(algorithm, cargoItemList);

        if (!cargoVanList.isListSizeLessOrEqualThanMaxSize(botService.getVanLimitFromCache(String.valueOf(chatId)))) {
            sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Не удалось распределить посылки из файла по указанному количеству фургонов"));
            returnToStart(chatId);
            return;
        }

        sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Результат распределения посылок по грузовым фургонам:"));
        sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "```" + cargoVanList.getCargoVanListAsString(cargoConverterService) + "```"));

        String jsonFileName;
        try {
            jsonFileName = fileService.writeStringToFile(cargoConverterService.serializeLoadedVansToJson(cargoVanList));
        } catch (RuntimeException e) {
            LOGGER.error("processPickAlgorithmCommand: error while creating JSON file: {}", e.getMessage());
            sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Произошла ошибка при формировании файла с результатами распределения"));
            sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "```" + e.getMessage() + "```"));
            returnToStart(chatId);
            return;
        }

        sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Результат распределения в файле:"));
        sendDocument(String.valueOf(chatId), jsonFileName, RESULT_JSON_FILE_NAME);
        returnToStart(chatId);
    }

    private void processReadCargoVansCommand(long chatId, Update update) {
        CargoVanList cargoVanList;
        if (update.getMessage().hasDocument()) {
            try {
                cargoVanList = cargoConverterService.deserializeLoadedVansFromJson(
                        fileService.readFromFile(
                                botService.getFileFromUpdate(update, telegramClient)
                        )
                );
            } catch (RuntimeException e) {
                LOGGER.error("processReadCargoVansCommand: error while deserializing file: {}", e.getMessage());
                sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Произошла ошибка при обработке файла:"));
                sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "```" + e.getMessage() + "```"));
                returnToStart(chatId);
                return;
            }
        } else {
            try {
                cargoVanList = cargoConverterService.deserializeLoadedVansFromJson(update.getMessage().getText());
            } catch (RuntimeException e) {
                LOGGER.error("processReadCargoVansCommand: error while deserializing JSON from message: {}", e.getMessage());
                sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Произошла ошибка при обработке JSON из сообщения:"));
                sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "```" + e.getMessage() + "```"));
                returnToStart(chatId);
                return;
            }
        }

        sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Количество обнаруженных фургонов: " + cargoVanList.getCargoVans().size()));
        sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Распределение посылок:"));
        sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "```" + cargoVanList.getCargoVanListAsString(cargoConverterService) + "```"));
        sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Общий список посылок из файла:"));
        sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "```" + cargoVanList.getAllCargoItemNamesAsString() + "```"));
        sendMessage(botService.buildTextMessageWithoutKeyboard(chatId, "Общее количество посылок из файла: " + cargoVanList.getAllCargoItemsFromVans().size()));
        returnToStart(chatId);
    }

    private void returnToStart(long chatId) {
        sendMessage(botService.buildTextMessageWithKeyboard(chatId, "Возвращаюсь в начало", CargoDistributorBotKeyboard.START));
    }

    private void processHelpCommand(long chatId) {
        sendSticker(chatId, HELP_COMMAND_STICKER_ID);
        sendMessage(botService.buildTextMessageWithoutKeyboard(chatId,
                """
                        Бот умеет распределять посылки по заданному количеству фургонов используя алгоритм по выбору.
                        Посылки берутся из файла, максимальный размер посылки - 9 клеток, посылки могут быть только прямоугольными
                        Грузовой фургон двумерный, размером 6x6 клеток
                        Пример файла:"""
        ));
        sendDocument(String.valueOf(chatId), CARGO_FILE_EXAMPLE_PATH, CARGO_FILE_EXAMPLE_NAME);
        sendMessage(botService.buildTextMessageWithoutKeyboard(chatId,
                """
                        Для запуска функции распределения используй команду /distribute
                        Или используй команду /start и нажми на соответствующую кнопку"""
        ));
        sendMessage(botService.buildTextMessageWithoutKeyboard(chatId,
                """
                        Еще бот умеет считывать загруженные файлы из JSON.
                        Можешь скинуть JSON файлом или прямо в сообщении
                        Пример файла:"""
        ));
        sendDocument(String.valueOf(chatId), LOADED_VANS_FILE_EXAMPLE_PATH, LOADED_VANS_FILE_EXAMPLE_NAME);
        sendMessage(botService.buildTextMessageWithoutKeyboard(chatId,
                """
                        Для запуска функции считывания используй команду /readcargo
                        Или используй команду /start и нажми на соответствующую кнопку"""
        ));
        returnToStart(chatId);
    }

    private void processAboutCommand(long chatId) {
        sendVideoNote(chatId, ABOUT_COMMAND_VIDEO_ID);
        sendSticker(chatId, ABOUT_COMMAND_STICKER_ID_1);
        sendSticker(chatId, ABOUT_COMMAND_STICKER_ID_2);
        sendSticker(chatId, ABOUT_COMMAND_STICKER_ID_3);
        returnToStart(chatId);
    }

    private void sendMessage(SendMessage message) {
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error("sendMessage: {}", e.getMessage());
            return;
        }
        botService.putLastMessageToCache(message.getChatId(), message);
    }

    private void sendDocument(String chat_id, String filePath, String fileName) {
        SendDocument sendDocument = new SendDocument(chat_id, new InputFile(new File(filePath), fileName));
        try {
            telegramClient.execute(sendDocument);
        } catch (TelegramApiException e) {
            LOGGER.error("sendDocument: {}", e.getMessage());
        }
    }

    private void sendSticker(long chatId, String stickerFileId) {
        SendSticker sendSticker = new SendSticker(String.valueOf(chatId), new InputFile(stickerFileId));
        try {
            telegramClient.execute(sendSticker);
        } catch (TelegramApiException e) {
            LOGGER.error("sendSticker: {}", e.getMessage());
        }
    }

    private void sendVideoNote(long chatId, String fileId) {
        SendVideoNote sendVideoNote = new SendVideoNote(String.valueOf(chatId), new InputFile(fileId));
        try {
            telegramClient.execute(sendVideoNote);
        } catch (TelegramApiException e) {
            LOGGER.error("sendVideoNote: {}", e.getMessage());
        }
    }

    private boolean updateHasMessageText(Update update) {
        if (update.getMessage().hasText()) {
            return update.getMessage().getText() != null && !update.getMessage().getText().isBlank() && !update.getMessage().getText().isEmpty();
        }
        return false;
    }

    private boolean isLastSendMessageEqualTo(long chatId, String messageText) {
        SendMessage message = botService.getLastSendMessageFromCache(String.valueOf(chatId));
        if (message == null) {
            return false;
        }
        return message
                .getText()
                .equals(messageText);
    }
}