package ru.liga.cargodistributor.bot.serviceImpls.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.LinkedList;
import java.util.List;

public class AboutCommandHandlerService extends CommandHandlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AboutCommandHandlerService.class);

    private static final String VIDEO_ID = "BAACAgIAAxkBAAPnZiRfZOTOCEhcoD205Iz6fDSiTbMAAqZTAALkTSBJd9KpquJL27A0BA";
    private static final String STICKER_ID_1 = "CAACAgIAAxkBAAEL9b5mJFsmbk4g6TvaIYybFKi2wDtuNQACSSwAAnodcUrY0E6TPhUOwjQE";
    private static final String STICKER_ID_2 = "CAACAgIAAxkBAAEL9cBmJFsw7zOK8bp2_y8wmFE1DM-3WAACMC8AAnXKaEp_pe78p-vsCzQE";
    private static final String STICKER_ID_3 = "CAACAgIAAxkBAAEL9cJmJFs8iNpo14WkcPqPNVfFtK3-CQACqi0AAmTJaUqOKPZ9kj4LQjQE";

    public AboutCommandHandlerService(TelegramClient telegramClient, CargoDistributorBotService botService, CargoConverterService cargoConverterService, FileService fileService) {
        super(telegramClient, botService, cargoConverterService, fileService);
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommandAndGetResponseMessages(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
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
                        chatId,
                        STICKER_ID_3
                )
        );

        returnToStart(chatId, resultResponse);
        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
