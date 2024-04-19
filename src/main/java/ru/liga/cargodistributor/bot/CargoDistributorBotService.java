package ru.liga.cargodistributor.bot;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.cargo.CargoItemList;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class CargoDistributorBotService {

    private final Map<String, SendMessage> lastSendMessages;
    private final Map<String, CargoItemList> lastReceivedCargo;

    public CargoDistributorBotService() {
        this.lastSendMessages = new HashMap<>();
        this.lastReceivedCargo = new HashMap<>();
    }

    public SendMessage buildTextMessage(long chatId, String content) {
        return SendMessage // Create a message object
                .builder()
                .chatId(chatId)
                .text(content)
                .build();
    }

    public void addLastSendMessage(String chatId, SendMessage message) {
        lastSendMessages.put(chatId, message);
    }

    public SendMessage getLastSendMessageByChatId(String chatId) {
        return lastSendMessages.getOrDefault(chatId, null);
    }

    public void addLastReceivedCargo(String chatId, CargoItemList cargoItemList) {
        lastReceivedCargo.put(chatId, cargoItemList);
    }

    public CargoItemList getLastReceivedCargoByChatId(String chatId) {
        return lastReceivedCargo.getOrDefault(chatId, null);
    }

    public File getFile(Update update, TelegramClient telegramClient) {
        Document document = update.getMessage().getDocument();
        GetFile getFileMethod = new GetFile(document.getFileId());
        org.telegram.telegrambots.meta.api.objects.File file = null;
        try {
            file = telegramClient.execute(getFileMethod);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        try {
            return telegramClient.downloadFile(file.getFilePath());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
