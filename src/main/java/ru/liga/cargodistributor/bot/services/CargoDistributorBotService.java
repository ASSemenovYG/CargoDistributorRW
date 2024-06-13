package ru.liga.cargodistributor.bot.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.algorithm.CargoDistributionParameters;
import ru.liga.cargodistributor.bot.CargoDistributorBotChatData;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotKeyboard;
import ru.liga.cargodistributor.bot.exceptions.GetFileFromUpdateException;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;
import ru.liga.cargodistributor.cargo.entity.CargoVanTypeInfo;

import java.io.File;

@Service
public class CargoDistributorBotService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CargoDistributorBotService.class);

    private final Cache<String, CargoDistributorBotChatData> cache;

    @Autowired
    public CargoDistributorBotService(@Value("${cache.capacity}") int cacheCapacity) {
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(cacheCapacity)
                .build(new CacheLoader<String, CargoDistributorBotChatData>() {
                    @Override
                    public CargoDistributorBotChatData load(String key) {
                        return new CargoDistributorBotChatData();
                    }
                });
    }

    //todo: возможно, все методы из этого класса можно выкинуть в CommandHandler и класс бота

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
                        .keyboard(CargoDistributorBotKeyboard.getKeyboardRows(keyboard))
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
        if (cache.getIfPresent(chatId) == null) {
            LOGGER.debug("putCargoItemListToCache: creating new cache for chatId {}", chatId);
            cache.put(chatId, CargoDistributorBotChatData.builder()
                    .cargoItemList(cargoItemList)
                    .build()
            );
            return;
        }
        getBotChatDataFromCache(chatId).setCargoItemList(cargoItemList);
    }

    public void putLastMessageToCache(String chatId, SendMessage message) {
        if (cache.getIfPresent(chatId) == null) {
            LOGGER.debug("putLastMessageToCache: creating new cache for chatId {}", chatId);
            cache.put(chatId, CargoDistributorBotChatData.builder()
                    .lastMessage(message)
                    .build()
            );
            return;
        }
        getBotChatDataFromCache(chatId).setLastMessage(message);
    }

    public void putVanLimitToCache(String chatId, int vanLimit) {
        if (cache.getIfPresent(chatId) == null) {
            LOGGER.debug("putVanLimitToCache: creating new cache for chatId {}", chatId);
            cache.put(chatId, CargoDistributorBotChatData.builder()
                    .vanLimit(vanLimit)
                    .build()
            );
            return;
        }
        getBotChatDataFromCache(chatId).setVanLimit(vanLimit);
    }

    public void putCargoItemTypeNameToCache(String chatId, String cargoItemTypeName) {
        if (cache.getIfPresent(chatId) == null) {
            LOGGER.debug("putCargoItemTypeNameToCache: creating new cache for chatId {}", chatId);
            cache.put(chatId, CargoDistributorBotChatData.builder()
                    .cargoItemTypeName(cargoItemTypeName)
                    .build()
            );
            return;
        }
        getBotChatDataFromCache(chatId).setCargoItemTypeName(cargoItemTypeName);
    }

    public void putCargoItemTypeLegendToCache(String chatId, String cargoItemTypeLegend) {
        if (cache.getIfPresent(chatId) == null) {
            LOGGER.debug("putCargoItemTypeLegendToCache: creating new cache for chatId {}", chatId);
            cache.put(chatId, CargoDistributorBotChatData.builder()
                    .cargoItemTypeLegend(cargoItemTypeLegend)
                    .build()
            );
            return;
        }
        getBotChatDataFromCache(chatId).setCargoItemTypeLegend(cargoItemTypeLegend);
    }

    public void putCargoItemTypeIntoToUpdateToCache(String chatId, CargoItemTypeInfo cargoItemTypeInfoToUpdate) {
        if (cache.getIfPresent(chatId) == null) {
            LOGGER.debug("putCargoItemTypeIntoToUpdateToCache: creating new cache for chatId {}", chatId);
            cache.put(chatId, CargoDistributorBotChatData.builder()
                    .cargoItemTypeInfoToUpdate(cargoItemTypeInfoToUpdate)
                    .build()
            );
            return;
        }
        getBotChatDataFromCache(chatId).setCargoItemTypeInfoToUpdate(cargoItemTypeInfoToUpdate);
    }

    public void putCargoVanTypeInfoToCache(String chatId, CargoVanTypeInfo cargoVanTypeInfo) {
        if (cache.getIfPresent(chatId) == null) {
            LOGGER.debug("putCargoVanTypeInfoToCache: creating new cache for chatId {}", chatId);
            cache.put(chatId, CargoDistributorBotChatData.builder()
                    .cargoVanTypeInfo(cargoVanTypeInfo)
                    .build()
            );
            return;
        }
        getBotChatDataFromCache(chatId).setCargoVanTypeInfo(cargoVanTypeInfo);
    }

    public void putCargoDistributionParametersToCache(String chatId, CargoDistributionParameters cargoDistributionParameters) {
        if (cache.getIfPresent(chatId) == null) {
            LOGGER.debug("putCargoDistributionParametersToCache: creating new cache for chatId {}", chatId);
            cache.put(chatId, CargoDistributorBotChatData.builder()
                    .cargoDistributionParameters(cargoDistributionParameters)
                    .build()
            );
            return;
        }
        getBotChatDataFromCache(chatId).setCargoDistributionParameters(cargoDistributionParameters);
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

    public CargoItemTypeInfo getCargoItemTypeInfoToUpdateFromCache(String chatId) {
        CargoDistributorBotChatData chatData = getBotChatDataFromCache(chatId);
        if (chatData == null) {
            LOGGER.debug("getCargoItemTypeInfoToUpdateFromCache: couldn't find cache for chatId {}", chatId);
            return null;
        }
        return chatData.getCargoItemTypeInfoToUpdate();
    }

    public CargoVanTypeInfo getCargoVanTypeInfoFromCache(String chatId) {
        CargoDistributorBotChatData chatData = getBotChatDataFromCache(chatId);
        if (chatData == null) {
            LOGGER.debug("getCargoVanTypeInfoFromCache: couldn't find cache for chatId {}", chatId);
            return null;
        }
        return chatData.getCargoVanTypeInfo();
    }

    public CargoDistributionParameters getCargoDistributionParametersFromCache(String chatId) {
        CargoDistributorBotChatData chatData = getBotChatDataFromCache(chatId);
        if (chatData == null) {
            LOGGER.debug("getCargoDistributionParametersFromCache: couldn't find cache for chatId {}", chatId);
            return null;
        }
        return chatData.getCargoDistributionParameters();
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
        return cache.getIfPresent(chatId);
    }
}
