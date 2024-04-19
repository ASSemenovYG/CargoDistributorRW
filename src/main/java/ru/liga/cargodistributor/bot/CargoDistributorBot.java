package ru.liga.cargodistributor.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
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

@Component
public class CargoDistributorBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CargoDistributorBot.class);

/*    @Value("${bot.token}")
    @NotNull
    private String token;*/

    private final TelegramClient telegramClient;
    private final CargoDistributorBotService botService;

    //todo: разобраться, почему аннотация не вытаскивает значение из application.properties
    public CargoDistributorBot(@Value("${bot.token}") String token) {
        //telegramClient = new OkHttpTelegramClient(getBotToken());
        //Environment env = new StandardReactiveWebEnvironment();
        //String token = env.getProperty("bot.token");
        //System.out.println(token);
        telegramClient = new OkHttpTelegramClient(token);
        botService = new CargoDistributorBotService();
    }

    @Override
    public String getBotToken() {
        return "token";
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        String message_text = update.getMessage().getText();
        long chat_id = update.getMessage().getChatId();
        if (message_text.equals("/start")) {
            // User send /start
            SendMessage message = botService.buildTextMessage(chat_id, "Вот что я могу:");

            message.setReplyMarkup(ReplyKeyboardMarkup
                    .builder()
                    .keyboardRow(new KeyboardRow(
                            "Прочитать посылки из файла и разложить по фургонам",
                            "Прочитать json с загруженными фургонами")
                    )
                    .build());
            sendMessage(message);
        } else if (message_text.equals("Прочитать посылки из файла и разложить по фургонам")) {
            //SendMessage message = botService.buildTextMessage(chat_id, "Отправь мне файл с посылками");
            sendMessage(botService.buildTextMessage(chat_id, "Отправь мне файл с посылками"));
        } else if (message_text.equals("Прочитать json с загруженными фургонами")) {
            sendMessage(botService.buildTextMessage(chat_id, "Отправь мне файл с загруженными фургонами или скинь json в сообщении"));
        } else if (update.getMessage().hasDocument() &&
                botService.getLastSendMessageByChatId(String.valueOf(update.getMessage().getChatId()))
                        .getText()
                        .equals("Отправь мне файл с посылками")
        ) {
            botService.getFile(update, telegramClient);
            CargoConverterService cargoConverterService = new CargoConverterService();
            FileService fileService = new FileService();
            CargoItemList cargoList = new CargoItemList(
                    cargoConverterService.parseCargoItems(
                            fileService.readFromFile(
                                    botService.getFile(update, telegramClient)
                            )
                    )
            );
            if (cargoList.isEmptyOrNull()) {
                sendMessage(botService.buildTextMessage(chat_id, "В файле не найдено ни одной посылки!"));
                return;
            }
            sendMessage(botService.buildTextMessage(chat_id, "В файле найдены следующие посылки:"));
            sendMessage(botService.buildTextMessage(chat_id, cargoList.getCargoItemNamesAsString()));
            botService.addLastReceivedCargo(String.valueOf(chat_id), cargoList);

            sendMessage(botService.buildTextMessage(chat_id, "Введи максимальное количество грузовых фургонов для распределения"));
            //todo: написать кеш с данными для расчета по chat_Id

            SendMessage pickAlgorithmMessage = botService.buildTextMessage(chat_id, "Выбери алгоритм распределения");
            pickAlgorithmMessage.setReplyMarkup(ReplyKeyboardMarkup
                    .builder()
                    .keyboardRow(new KeyboardRow(
                            "OneVanOneItemDistribution",
                            "SingleSortedCargoDistribution",
                            "SimpleFitDistribution")
                    )
                    .build());

            sendMessage(pickAlgorithmMessage);
        } else if (botService.getLastSendMessageByChatId(String.valueOf(update.getMessage().getChatId()))
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
                //TODO: код на переотправку сообщения с выбором алгоритма
                return;
            }
            CargoItemList cargoItemList = botService.getLastReceivedCargoByChatId(String.valueOf(chat_id));
            if (cargoItemList.isEmptyOrNull()) {
                //todo: "я не нашел чето твой список посылок, отправь еще раз"
                return;
            }
            CargoVanList cargoVanList = new CargoVanList();
            cargoVanList.distributeCargo(algorithm, cargoItemList);
        }

    }

    private void sendMessage(SendMessage message) {
        try {
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            LOGGER.error("sendMessage: {}", e.getMessage());
            return;
        }
        botService.addLastSendMessage(message.getChatId(), message);
    }
}