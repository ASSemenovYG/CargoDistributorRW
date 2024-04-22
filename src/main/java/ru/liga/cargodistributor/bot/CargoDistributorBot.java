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
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.cargo.CargoConverterService;
import ru.liga.cargodistributor.util.FileService;

import java.util.List;

@Component
public class CargoDistributorBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CargoDistributorBot.class);

    private final String token;

    private final TelegramClient telegramClient;
    private final CargoDistributorBotService botService;
    private final CargoDistributorBotUpdateHandler updateHandler;

    @Autowired
    public CargoDistributorBot(@Value("${bot.token}") String token, @Value("${cache.capacity}") int cacheCapacity) {
        this.token = token;
        this.telegramClient = new OkHttpTelegramClient(getBotToken());
        this.botService = new CargoDistributorBotService(cacheCapacity);
        this.updateHandler = new CargoDistributorBotUpdateHandler(this.telegramClient, this.botService, new CargoConverterService(), new FileService());
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
        List<Object> responseMessages = updateHandler.processUpdateAndGetResponseMessages(update);

        for (Object responseMessage : responseMessages) {
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
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error("sendMessage: {}", e.getMessage());
            return;
        }
        botService.putLastMessageToCache(message.getChatId(), message);
    }

    private void sendDocument(SendDocument sendDocument) {
        try {
            telegramClient.execute(sendDocument);
        } catch (TelegramApiException e) {
            LOGGER.error("sendDocument: {}", e.getMessage());
        }
    }

    private void sendSticker(SendSticker sendSticker) {
        try {
            telegramClient.execute(sendSticker);
        } catch (TelegramApiException e) {
            LOGGER.error("sendSticker: {}", e.getMessage());
        }
    }

    private void sendVideoNote(SendVideoNote sendVideoNote) {
        try {
            telegramClient.execute(sendVideoNote);
        } catch (TelegramApiException e) {
            LOGGER.error("sendVideoNote: {}", e.getMessage());
        }
    }
}