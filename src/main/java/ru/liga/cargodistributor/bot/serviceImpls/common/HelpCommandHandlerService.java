package ru.liga.cargodistributor.bot.serviceImpls.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;

import java.util.LinkedList;
import java.util.List;

public class HelpCommandHandlerService extends CommandHandlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelpCommandHandlerService.class);

    private static final String SRC_RESOURCES_PATH = "src/main/resources/";
    private static final String CARGO_FILE_EXAMPLE_NAME = "cargo_file_example.pkg";
    private static final String LOADED_VANS_FILE_EXAMPLE_NAME = "loadedVansExample.json";
    private static final String CARGO_FILE_EXAMPLE_PATH = SRC_RESOURCES_PATH + CARGO_FILE_EXAMPLE_NAME;
    private static final String LOADED_VANS_FILE_EXAMPLE_PATH = SRC_RESOURCES_PATH + LOADED_VANS_FILE_EXAMPLE_NAME;
    private static final String STICKER_ID = "CAACAgIAAxkBAAEL9bxmJFPTU9efBMHORW2P0MoLt4KSWQACWwIAAvNWPxdFcg4Bd_Sh0zQE";

    public HelpCommandHandlerService(CargoDistributorBotService botService) {
        super(botService);
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommandAndGetResponseMessages(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);

        resultResponse.add(
                botService.buildStickerMessage(
                        chatId,
                        STICKER_ID
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.HELP_DISTRIBUTE_COMMAND_DESCRIPTION.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildDocumentMessage(
                        chatId,
                        CARGO_FILE_EXAMPLE_PATH,
                        CARGO_FILE_EXAMPLE_NAME
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.HELP_DISTRIBUTE_COMMAND_RUN.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.HELP_READCARGO_COMMAND_DESCRIPTION.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildDocumentMessage(
                        chatId,
                        LOADED_VANS_FILE_EXAMPLE_PATH,
                        LOADED_VANS_FILE_EXAMPLE_NAME
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.HELP_READCARGO_COMMAND_RUN.getMessageText()
                )
        );

        returnToStart(chatId, resultResponse);
        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
