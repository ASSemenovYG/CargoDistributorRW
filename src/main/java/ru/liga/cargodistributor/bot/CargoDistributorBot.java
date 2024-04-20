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
        String message_text = update.getMessage().getText();
        long chat_id = update.getMessage().getChatId();

        if (message_text != null && !message_text.isBlank() && message_text.equals("/start")) {
            // User send /start
/*            SendMessage message = botService.buildTextMessageWithoutKeyboard(chat_id, "Вот что я могу:");

            message.setReplyMarkup(ReplyKeyboardMarkup
                    .builder()
                    .keyboardRow(new KeyboardRow(
                            "Прочитать посылки из файла и разложить по фургонам",
                            "Прочитать json с загруженными фургонами")
                    )
                    .build());
            sendMessage(message);*/
            sendMessage(botService.buildTextMessageWithKeyboard(chat_id,"Вот что я могу:", CargoDistributorBotKeyboard.START));
        } else if (message_text != null && !message_text.isBlank() && message_text.equals("Прочитать посылки из файла и разложить по фургонам")) {
            sendMessage(botService.buildTextMessageWithoutKeyboard(chat_id, "Отправь мне файл с посылками"));
        } else if (message_text != null && !message_text.isBlank() && message_text.equals("Прочитать json с загруженными фургонами")) {
            sendMessage(botService.buildTextMessageWithoutKeyboard(chat_id, "Отправь мне файл с загруженными фургонами или скинь json в сообщении"));
        } else if (message_text == null && update.getMessage().hasDocument() &&
                botService.getLastSendMessageFromCache(String.valueOf(update.getMessage().getChatId()))
                        .getText()
                        .equals("Отправь мне файл с посылками")
        ) {
            botService.getFileFromUpdate(update, telegramClient);
            CargoItemList cargoList = new CargoItemList(
                    cargoConverterService.parseCargoItems(
                            fileService.readFromFile(
                                    botService.getFileFromUpdate(update, telegramClient)
                            )
                    )
            );
            if (cargoList.isEmptyOrNull()) {
                sendMessage(botService.buildTextMessageWithoutKeyboard(chat_id, "В файле не найдено ни одной посылки!"));
                return;
            }
            sendMessage(botService.buildTextMessageWithoutKeyboard(chat_id, "В файле найдены следующие посылки:"));
            sendMessage(botService.buildTextMessageWithoutKeyboard(chat_id, cargoList.getCargoItemNamesAsString()));
            //botService.addLastReceivedCargo(String.valueOf(chat_id), cargoList);
            botService.putCargoItemListToCache(String.valueOf(chat_id), cargoList);

            sendMessage(botService.buildTextMessageWithoutKeyboard(chat_id, "Введи максимальное количество грузовых фургонов для распределения"));
            return;
        } else if (message_text != null &&
                !message_text.isBlank() &&
                botService.getLastSendMessageFromCache(String.valueOf(update.getMessage().getChatId()))
                        .getText()
                        .equals("Введи максимальное количество грузовых фургонов для распределения")
        ) {
            botService.putVanLimitToCache(String.valueOf(chat_id), Integer.parseInt(message_text));

/*            botService.buildTextMessageWithKeyboard(String.valueOf(chat_id),"Выбери алгоритм распределения", CargoDistributorBotKeyboard.PICK_ALGORITHM);
            SendMessage pickAlgorithmMessage = botService.buildTextMessageWithoutKeyboard(chat_id, "Выбери алгоритм распределения");
            pickAlgorithmMessage.setReplyMarkup(ReplyKeyboardMarkup
                    .builder()
                    .keyboardRow(new KeyboardRow(
                            "OneVanOneItemDistribution",
                            "SingleSortedCargoDistribution",
                            "SimpleFitDistribution")
                    )
                    .build());*/

            sendMessage(botService.buildTextMessageWithKeyboard(chat_id,"Выбери алгоритм распределения", CargoDistributorBotKeyboard.PICK_ALGORITHM));

        } else if (message_text != null && !message_text.isBlank() && botService.getLastSendMessageFromCache(String.valueOf(update.getMessage().getChatId()))
                .getText()
                .equals("Выбери алгоритм распределения")) {
            DistributionAlgorithmService algorithm = null;
            try {
                algorithm = switch (message_text) {
                    case "OneVanOneItemDistribution" -> new OneVanOneItemDistributionAlgorithmService();
                    case "SingleSortedCargoDistribution" -> new SingleSortedCargoDistributionAlgorithmService();
                    case "SimpleFitDistribution" -> new SimpleFitDistributionAlgorithmService();
                    default -> throw new IllegalArgumentException("Введен неверный код алгоритма");
                };
            } catch (IllegalArgumentException e) {
                LOGGER.error(e.getMessage());
                //TODO: мб следует запихнуть это в отдельный метод в сервисе
                sendMessage(botService.buildTextMessageWithoutKeyboard(chat_id, "Я не понял, какой алгоритм ты выбрал"));


/*                SendMessage pickAlgorithmMessage = botService.buildTextMessageWithoutKeyboard(chat_id, "Выбери алгоритм распределения");
                //todo: добавить в сервисе отдельный метод для генерации сообщения с клавиатурой
                pickAlgorithmMessage.setReplyMarkup(ReplyKeyboardMarkup
                        .builder()
                        .keyboardRow(new KeyboardRow(
                                "OneVanOneItemDistribution",
                                "SingleSortedCargoDistribution",
                                "SimpleFitDistribution")
                        )
                        .build());

                sendMessage(pickAlgorithmMessage);*/
                sendMessage(botService.buildTextMessageWithKeyboard(chat_id,"Выбери алгоритм распределения", CargoDistributorBotKeyboard.PICK_ALGORITHM));
                return;
            }
            CargoItemList cargoItemList = botService.getCargoItemListFromCache(String.valueOf(chat_id));
            if (cargoItemList.isEmptyOrNull()) {
                sendMessage(botService.buildTextMessageWithoutKeyboard(chat_id, "Я не нашел твой список посылок"));
                sendMessage(botService.buildTextMessageWithoutKeyboard(chat_id, "Отправь мне файл с посылками"));
                return;
            }
            CargoVanList cargoVanList = new CargoVanList();
            cargoVanList.distributeCargo(algorithm, cargoItemList);

            if (!cargoVanList.isListSizeLessOrEqualThanMaxSize(botService.getVanLimitFromCache(String.valueOf(chat_id)))) {
                sendMessage(botService.buildTextMessageWithoutKeyboard(chat_id, "Не удалось распределить посылки из файла по указанному количеству фургонов"));
                return;
            }

            sendMessage(botService.buildTextMessageWithoutKeyboard(chat_id, "Результат распределения посылок по грузовым фургонам:"));
            sendMessage(botService.buildTextMessageWithoutKeyboard(chat_id, "```" + cargoVanList.getCargoVanListAsString(cargoConverterService) + "```"));

            String jsonFileName = fileService.writeStringToFile(cargoConverterService.serializeLoadedVansToJson(cargoVanList));
            sendMessage(botService.buildTextMessageWithoutKeyboard(chat_id, "Результат распределения в файле:"));
            sendDocument(String.valueOf(chat_id),jsonFileName);
        }

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

    private void sendDocument(String chat_id, String fileName) {
        SendDocument sendDocument = new SendDocument(chat_id, new InputFile(new File(fileName), "loadedVans.json"));
        try {
            telegramClient.execute(sendDocument);
        } catch (TelegramApiException e) {
            LOGGER.error("sendDocument: {}", e.getMessage());
        }
    }
}