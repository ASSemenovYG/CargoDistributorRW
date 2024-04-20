package ru.liga.cargodistributor.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.CargoVanList;
import ru.liga.cargodistributor.util.LruCache;

import java.io.File;

@Service
public class CargoDistributorBotService {
    private final LruCache cache;

    public CargoDistributorBotService(@Value("${cache.capacity}") int cacheCapacity) {
        this.cache = new LruCache(cacheCapacity);
    }

    public SendMessage buildTextMessageWithoutKeyboard(long chatId, String content) {
        return SendMessage
                .builder()
                .chatId(chatId)
                .parseMode(ParseMode.MARKDOWN)
                .text(content)
                .replyMarkup(ReplyKeyboardRemove.builder()
                        .removeKeyboard(true)
                        .build())
                .build();
    }

    public SendMessage buildTextMessageWithKeyboard(long chatId, String content, CargoDistributorBotKeyboard keyboard) {
        return SendMessage
                .builder()
                .chatId(chatId)
                .parseMode(ParseMode.MARKDOWN)
                .text(content)
                .replyMarkup(ReplyKeyboardMarkup
                        .builder()
                        .keyboardRow(new KeyboardRow(getKeyboardButtons(keyboard))                        )
                        .build())
                .build();
    }

    public void putCargoItemListToCache(String chatId, CargoItemList cargoItemList) {
        if (cache.get(chatId) == null) {
            cache.put(chatId, new CargoDistributorBotChatData(cargoItemList));
            return;
        }
        getBotChatDataFromCache(chatId).setCargoItemList(cargoItemList);
    }

    public void putLastMessageToCache(String chatId, SendMessage message) {
        if (cache.get(chatId) == null) {
            cache.put(chatId, new CargoDistributorBotChatData(message));
            return;
        }
        getBotChatDataFromCache(chatId).setLastMessage(message);
    }

    public void putCargoVanListToCache(String chatId, CargoVanList cargoVanList) {
        if (cache.get(chatId) == null) {
            cache.put(chatId, new CargoDistributorBotChatData(cargoVanList));
            return;
        }
        getBotChatDataFromCache(chatId).setCargoVanList(cargoVanList);
    }

    public void putVanLimitToCache(String chatId, int vanLimit) {
        if (cache.get(chatId) == null) {
            cache.put(chatId, new CargoDistributorBotChatData(vanLimit));
            return;
        }
        getBotChatDataFromCache(chatId).setVanLimit(vanLimit);
    }

    public SendMessage getLastSendMessageFromCache(String chatId) {
        return getBotChatDataFromCache(chatId).getLastMessage();
    }

    public CargoItemList getCargoItemListFromCache(String chatId) {
        return getBotChatDataFromCache(chatId).getCargoItemList();
    }

    public int getVanLimitFromCache(String chatId) {
        return getBotChatDataFromCache(chatId).getVanLimit();
    }

    public File getFileFromUpdate(Update update, TelegramClient telegramClient) {
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

    private CargoDistributorBotChatData getBotChatDataFromCache(String chatId) {
        return (CargoDistributorBotChatData) cache.get(chatId);
    }

    private String[] getKeyboardButtons(CargoDistributorBotKeyboard keyboard) {
        switch (keyboard) {
            case START -> {
                return new String[]{"Прочитать посылки из файла и разложить по фургонам", "Прочитать json с загруженными фургонами"};
            }
            case PICK_ALGORITHM -> {
                return new String[]{"OneVanOneItemDistribution", "SingleSortedCargoDistribution", "SimpleFitDistribution"};
            }
        }
        return null;
    }
}
