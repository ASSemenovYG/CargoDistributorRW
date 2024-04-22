package ru.liga.cargodistributor.bot.commandhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.CargoDistributorBotService;
import ru.liga.cargodistributor.cargo.CargoConverterService;
import ru.liga.cargodistributor.util.FileService;

import java.util.LinkedList;
import java.util.List;

@Service
public class AboutCommandHandlerService extends CommandHandlerService {
    private static final String VIDEO_ID = "BAACAgIAAxkBAAPnZiRfZOTOCEhcoD205Iz6fDSiTbMAAqZTAALkTSBJd9KpquJL27A0BA";
    private static final String STICKER_ID_1 = "CAACAgIAAxkBAAEL9b5mJFsmbk4g6TvaIYybFKi2wDtuNQACSSwAAnodcUrY0E6TPhUOwjQE";
    private static final String STICKER_ID_2 = "CAACAgIAAxkBAAEL9cBmJFsw7zOK8bp2_y8wmFE1DM-3WAACMC8AAnXKaEp_pe78p-vsCzQE";
    private static final String STICKER_ID_3 = "CAACAgIAAxkBAAEL9cJmJFs8iNpo14WkcPqPNVfFtK3-CQACqi0AAmTJaUqOKPZ9kj4LQjQE";

    @Autowired
    protected AboutCommandHandlerService(@Value("${bot.token}") String token, @Value("${cache.capacity}") int cacheCapacity) {
        super(token, cacheCapacity);
    }

    public AboutCommandHandlerService(TelegramClient telegramClient, CargoDistributorBotService botService, CargoConverterService cargoConverterService, FileService fileService) {
        super(telegramClient, botService, cargoConverterService, fileService);
    }

    @Override
    public List<Object> processCommandAndGetResponseMessages(Update update) {
        List<Object> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);

        resultResponse.add(
                botService.buildMessageWithVideo(
                        chatId,
                        VIDEO_ID
                )
        );

        resultResponse.add(
                botService.buildStickerMessage(
                        chatId,
                        STICKER_ID_1
                )
        );

        resultResponse.add(
                botService.buildStickerMessage(
                        chatId,
                        STICKER_ID_2
                )
        );

        resultResponse.add(
                botService.buildStickerMessage(
                        chatId, STICKER_ID_3
                )
        );

        returnToStart(chatId, resultResponse);

        return resultResponse;
    }
}