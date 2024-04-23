package ru.liga.cargodistributor.bot.commandhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.CargoDistributorBotService;
import ru.liga.cargodistributor.cargo.CargoConverterService;
import ru.liga.cargodistributor.util.FileService;

import java.util.LinkedList;
import java.util.List;

@Service
public class HelpCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(HelpCommandHandlerService.class);

    private static final String SRC_RESOURCES_PATH = "src/main/resources/";
    private static final String CARGO_FILE_EXAMPLE_NAME = "cargo_file_example.pkg";
    private static final String LOADED_VANS_FILE_EXAMPLE_NAME = "loadedVansExample.json";
    private static final String CARGO_FILE_EXAMPLE_PATH = SRC_RESOURCES_PATH + CARGO_FILE_EXAMPLE_NAME;
    private static final String LOADED_VANS_FILE_EXAMPLE_PATH = SRC_RESOURCES_PATH + LOADED_VANS_FILE_EXAMPLE_NAME;
    private static final String STICKER_ID = "CAACAgIAAxkBAAEL9bxmJFPTU9efBMHORW2P0MoLt4KSWQACWwIAAvNWPxdFcg4Bd_Sh0zQE";

    @Autowired
    protected HelpCommandHandlerService(@Value("${bot.token}") String token, @Value("${cache.capacity}") int cacheCapacity) {
        super(token, cacheCapacity);
    }

    public HelpCommandHandlerService(TelegramClient telegramClient, CargoDistributorBotService botService, CargoConverterService cargoConverterService, FileService fileService) {
        super(telegramClient, botService, cargoConverterService, fileService);
    }

    @Override
    public List<Object> processCommandAndGetResponseMessages(Update update) {
        LOGGER.info("Started processing command");
        List<Object> resultResponse = new LinkedList<>();
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
