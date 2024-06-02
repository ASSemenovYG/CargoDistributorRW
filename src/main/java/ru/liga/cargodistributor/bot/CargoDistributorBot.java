package ru.liga.cargodistributor.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.repository.CargoItemTypeRepository;
import ru.liga.cargodistributor.cargo.repository.CargoVanTypeRepository;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.List;

@Component
public class CargoDistributorBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CargoDistributorBot.class);

    private final String token;

    private final TelegramClient telegramClient;
    private final CargoDistributorBotService botService;
    private final CargoConverterService cargoConverterService;
    private final FileService fileService;

    @Autowired
    private CargoItemTypeRepository cargoItemTypeRepository;
    @Autowired
    private CargoVanTypeRepository cargoVanTypeRepository;

    @Autowired
    public CargoDistributorBot(@Value("${bot.token}") String token, @Value("${cache.capacity}") int cacheCapacity) {
        this.token = token;
        this.cargoConverterService = new CargoConverterService();
        this.fileService = new FileService();
        this.telegramClient = new OkHttpTelegramClient(getBotToken());
        this.botService = new CargoDistributorBotService(cacheCapacity);
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
        LOGGER.info("Receiving message: {}", update.toString());

        SendMessage lastSendMessage = botService.getLastSendMessageFromCache(String.valueOf(update.getMessage().getChatId()));
        CommandHandlerService handlerService = CommandHandlerService.determineAndGetCommandHandler(
                update, botService, lastSendMessage, telegramClient, cargoConverterService, fileService, cargoItemTypeRepository, cargoVanTypeRepository
        );

        //todo: Добавлять\\Изменять\\Редактировать\\Удалять кузовы
        //todo: добавить выгрузку всех типов кузовов
        //todo: процесс распределения посылок с типами кузовов и посылок
        //todo: приделать сохранение истории в бд под кешем
        List<PartialBotApiMethod<Message>> responseMessages = handlerService.processCommandAndGetResponseMessages(update);

        for (PartialBotApiMethod<Message> responseMessage : responseMessages) {
            if (responseMessage.getClass().equals(SendMessage.class)) {
                sendMessage((SendMessage) responseMessage);
            } else if (responseMessage.getClass().equals(SendDocument.class)) {
                sendDocument((SendDocument) responseMessage);
            } else if (responseMessage.getClass().equals(SendSticker.class)) {
                sendSticker((SendSticker) responseMessage);
            } else if (responseMessage.getClass().equals(SendVideoNote.class)) {
                sendVideoNote((SendVideoNote) responseMessage);
            }
        }
    }

    private void sendMessage(SendMessage message) {
        try {
            LOGGER.info("Sending response message: {}", message.toString());
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error("sendMessage: {}", e.getMessage());
            return;
        }
        botService.putLastMessageToCache(message.getChatId(), message);
    }

    private void sendDocument(SendDocument sendDocument) {
        try {
            LOGGER.info("Sending response message with document: {}", sendDocument.toString());
            telegramClient.execute(sendDocument);
        } catch (TelegramApiException e) {
            LOGGER.error("sendDocument: {}", e.getMessage());
        }
    }

    private void sendSticker(SendSticker sendSticker) {
        try {
            LOGGER.info("Sending response message with sticker: {}", sendSticker.toString());
            telegramClient.execute(sendSticker);
        } catch (TelegramApiException e) {
            LOGGER.error("sendSticker: {}", e.getMessage());
        }
    }

    private void sendVideoNote(SendVideoNote sendVideoNote) {
        try {
            LOGGER.info("Sending response message with video: {}", sendVideoNote.toString());
            telegramClient.execute(sendVideoNote);
        } catch (TelegramApiException e) {
            LOGGER.error("sendVideoNote: {}", e.getMessage());
        }
    }
}