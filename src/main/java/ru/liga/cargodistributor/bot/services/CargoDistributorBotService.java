package ru.liga.cargodistributor.bot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.CargoDistributorBotChatData;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotKeyboard;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotKeyboardButton;
import ru.liga.cargodistributor.bot.exceptions.GetFileFromUpdateException;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.util.LruCache;

import java.io.File;
import java.util.List;

@Service
public class CargoDistributorBotService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CargoDistributorBotService.class);

    private final LruCache cache;

    @Autowired
    public CargoDistributorBotService(@Value("${cache.capacity}") int cacheCapacity) {
        this.cache = new LruCache(cacheCapacity);
    }

    public SendMessage buildTextMessageWithoutKeyboard(long chatId, String content) {
        LOGGER.debug("Building text message without keyboard for chatId {} ; content: {}", chatId, content);
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
        LOGGER.debug("Building text message with keyboard {} for chatId {} ; content: {}", keyboard, chatId, content);
        return SendMessage
                .builder()
                .chatId(chatId)
                .parseMode(ParseMode.MARKDOWN)
                .text(content)
                .replyMarkup(ReplyKeyboardMarkup
                        .builder()
                        .keyboard(getKeyboardRows(keyboard))
                        .build())
                .build();
    }

    public SendDocument buildDocumentMessage(long chatId, String filePath, String fileName) {
        LOGGER.debug("Building document message from file {} for chatId {} ; result file name: {}", filePath, chatId, fileName);
        return new SendDocument(String.valueOf(chatId), new InputFile(new File(filePath), fileName));
    }

    public SendSticker buildStickerMessage(long chatId, String stickerFileId) {
        LOGGER.debug("Building sticker message for chatId {} ; sticker id: {}", chatId, stickerFileId);
        return new SendSticker(String.valueOf(chatId), new InputFile(stickerFileId));
    }

    public SendVideoNote buildMessageWithVideo(long chatId, String fileId) {
        LOGGER.debug("Building message with video for chatId {} ; video file id: {}", chatId, fileId);
        return new SendVideoNote(String.valueOf(chatId), new InputFile(fileId));
    }

    public void putCargoItemListToCache(String chatId, CargoItemList cargoItemList) {
        if (cache.get(chatId) == null) {
            LOGGER.debug("putCargoItemListToCache: creating new cache for chatId {}", chatId);
            cache.put(chatId, new CargoDistributorBotChatData(cargoItemList));
            return;
        }
        getBotChatDataFromCache(chatId).setCargoItemList(cargoItemList);
    }

    public void putLastMessageToCache(String chatId, SendMessage message) {
        if (cache.get(chatId) == null) {
            LOGGER.debug("putLastMessageToCache: creating new cache for chatId {}", chatId);
            cache.put(chatId, new CargoDistributorBotChatData(message));
            return;
        }
        getBotChatDataFromCache(chatId).setLastMessage(message);
    }

    public void putVanLimitToCache(String chatId, int vanLimit) {
        if (cache.get(chatId) == null) {
            LOGGER.debug("putVanLimitToCache: creating new cache for chatId {}", chatId);
            cache.put(chatId, new CargoDistributorBotChatData(vanLimit));
            return;
        }
        getBotChatDataFromCache(chatId).setVanLimit(vanLimit);
    }

    public void putCargoItemTypeNameToCache(String chatId, String cargoItemTypeName) {
        if (cache.get(chatId) == null) {
            LOGGER.debug("putCargoItemTypeNameToCache: creating new cache for chatId {}", chatId);
            cache.put(chatId, new CargoDistributorBotChatData(cargoItemTypeName));
            return;
        }
        getBotChatDataFromCache(chatId).setCargoItemTypeName(cargoItemTypeName);
    }

    public void putCargoItemTypeLegendToCache(String chatId, String cargoItemTypeLegend) {
        if (cache.get(chatId) == null) {
            LOGGER.debug("putCargoItemTypeLegendToCache: creating new cache for chatId {}", chatId);
            cache.put(chatId, new CargoDistributorBotChatData(null, null, 0, null, cargoItemTypeLegend));
            return;
        }
        getBotChatDataFromCache(chatId).setCargoItemTypeLegend(cargoItemTypeLegend);
    }

    public SendMessage getLastSendMessageFromCache(String chatId) {
        CargoDistributorBotChatData chatData = getBotChatDataFromCache(chatId);
        if (chatData == null) {
            LOGGER.debug("getLastSendMessageFromCache: couldn't find cache for chatId {}", chatId);
            return null;
        }
        return chatData.getLastMessage();
    }

    public CargoItemList getCargoItemListFromCache(String chatId) {
        CargoDistributorBotChatData chatData = getBotChatDataFromCache(chatId);
        if (chatData == null) {
            LOGGER.debug("getCargoItemListFromCache: couldn't find cache for chatId {}", chatId);
            return null;
        }
        return chatData.getCargoItemList();
    }

    public int getVanLimitFromCache(String chatId) {
        CargoDistributorBotChatData chatData = getBotChatDataFromCache(chatId);
        if (chatData == null) {
            LOGGER.debug("getVanLimitFromCache: couldn't find cache for chatId {}", chatId);
            return 0;
        }
        return chatData.getVanLimit();
    }

    public String getCargoItemTypeLegendFromCache(String chatId) {
        CargoDistributorBotChatData chatData = getBotChatDataFromCache(chatId);
        if (chatData == null) {
            LOGGER.debug("getCargoItemTypeLegendFromCache: couldn't find cache for chatId {}", chatId);
            return null;
        }
        return chatData.getCargoItemTypeLegend();
    }

    public String getCargoItemTypeNameFromCache(String chatId) {
        CargoDistributorBotChatData chatData = getBotChatDataFromCache(chatId);
        if (chatData == null) {
            LOGGER.debug("getCargoItemTypeNameFromCache: couldn't find cache for chatId {}", chatId);
            return null;
        }
        return chatData.getCargoItemTypeName();
    }

    public File getFileFromUpdate(Update update, TelegramClient telegramClient) {
        Document document = update.getMessage().getDocument();
        GetFile getFileMethod = new GetFile(document.getFileId());
        org.telegram.telegrambots.meta.api.objects.File file;
        try {
            file = telegramClient.execute(getFileMethod);
        } catch (TelegramApiException e) {
            LOGGER.error("getFileFromUpdate.GetFile execute: {}", e.getMessage());
            throw new GetFileFromUpdateException(e.getMessage(), e);
        }
        try {
            return telegramClient.downloadFile(file.getFilePath());
        } catch (TelegramApiException e) {
            LOGGER.error("getFileFromUpdate.downloadFile: {}", e.getMessage());
            throw new GetFileFromUpdateException(e.getMessage(), e);
        }
    }

    private CargoDistributorBotChatData getBotChatDataFromCache(String chatId) {
        LOGGER.debug("getBotChatDataFromCache: getting cache for chatId {}", chatId);
        return (CargoDistributorBotChatData) cache.get(chatId);
    }

    private List<KeyboardRow> getKeyboardRows(CargoDistributorBotKeyboard keyboard) {
        switch (keyboard) {
            case START -> {
                return List.of(
                        new KeyboardRow(
                                CargoDistributorBotKeyboardButton.READ_CARGO_AND_DISTRIBUTE.getButtonText(),
                                CargoDistributorBotKeyboardButton.READ_JSON_WITH_LOADED_VANS.getButtonText()
                        ),
                        new KeyboardRow(
                                CargoDistributorBotKeyboardButton.ADD_CARGO_TYPE.getButtonText(),
                                CargoDistributorBotKeyboardButton.EDIT_CARGO_TYPE.getButtonText(),
                                CargoDistributorBotKeyboardButton.DELETE_CARGO_TYPE.getButtonText()
                        )
                );
            }
            case PICK_ALGORITHM -> {
                return List.of(
                        new KeyboardRow(
                                CargoDistributorBotKeyboardButton.ALGORITHM_ONE_VAN_ONE_ITEM.getButtonText(),
                                CargoDistributorBotKeyboardButton.ALGORITHM_SINGLE_SORTED.getButtonText(),
                                CargoDistributorBotKeyboardButton.ALGORITHM_SIMPLE_FIT.getButtonText()
                        )
                );
            }
        }
        return null;
    }
}
