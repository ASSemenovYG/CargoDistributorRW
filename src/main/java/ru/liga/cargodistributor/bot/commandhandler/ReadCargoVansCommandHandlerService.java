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
import ru.liga.cargodistributor.cargo.CargoVanList;
import ru.liga.cargodistributor.util.FileService;

import java.util.LinkedList;
import java.util.List;

@Service
public class ReadCargoVansCommandHandlerService extends CommandHandlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadCargoVansCommandHandlerService.class);

    @Autowired
    protected ReadCargoVansCommandHandlerService(@Value("${bot.token}") String token, @Value("${cache.capacity}") int cacheCapacity) {
        super(token, cacheCapacity);
    }

    public ReadCargoVansCommandHandlerService(TelegramClient telegramClient, CargoDistributorBotService botService, CargoConverterService cargoConverterService, FileService fileService) {
        super(telegramClient, botService, cargoConverterService, fileService);
    }

    @Override
    public List<Object> processCommandAndGetResponseMessages(Update update) {
        LOGGER.info("Started processing command");
        List<Object> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);

        CargoVanList cargoVanList;
        if (update.getMessage().hasDocument()) {
            LOGGER.info("Reading cargo vans from a file");
            try {
                cargoVanList = cargoConverterService.deserializeLoadedVansFromJson(
                        fileService.readFromFile(
                                botService.getFileFromUpdate(update, telegramClient)
                        )
                );
            } catch (RuntimeException e) {
                LOGGER.error("error while deserializing file: {}", e.getMessage());

                resultResponse.add(
                        botService.buildTextMessageWithoutKeyboard(
                                chatId,
                                CargoDistributorBotResponseMessage.ERROR_WHILE_PROCESSING_CARGO_VAN_FILE.getMessageText()
                        )
                );

                resultResponse.add(
                        botService.buildTextMessageWithoutKeyboard(
                                chatId,
                                "```" + e.getMessage() + "```"
                        )
                );

                returnToStart(chatId, resultResponse);
                LOGGER.info("Finished processing command, error occurred while reading JSON from file");
                return resultResponse;
            }
        } else {
            LOGGER.info("Reading JSON with cargo vans from a message");
            try {
                cargoVanList = cargoConverterService.deserializeLoadedVansFromJson(update.getMessage().getText());
            } catch (RuntimeException e) {
                LOGGER.error("error while deserializing JSON from message: {}", e.getMessage());

                resultResponse.add(
                        botService.buildTextMessageWithoutKeyboard(
                                chatId,
                                CargoDistributorBotResponseMessage.ERROR_WHILE_PROCESSING_CARGO_VAN_JSON_MESSAGE.getMessageText()
                        )
                );

                resultResponse.add(
                        botService.buildTextMessageWithoutKeyboard(
                                chatId,
                                "```" + e.getMessage() + "```"
                        )
                );

                returnToStart(chatId, resultResponse);
                LOGGER.info("Finished processing command, error occurred while reading JSON from message");
                return resultResponse;
            }
        }

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.NUMBER_OF_READ_VANS.getMessageText() + cargoVanList.getCargoVans().size()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.DISTRIBUTION_OF_CARGO_FROM_VANS.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        "```" + cargoVanList.getCargoVanListAsString(cargoConverterService) + "```"
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.CARGO_LIST_FROM_VANS.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        "```" + cargoVanList.getAllCargoItemNamesAsString() + "```"
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.NUMBER_OF_ITEMS_FROM_VANS.getMessageText() + cargoVanList.getAllCargoItemsFromVans().size())
        );

        returnToStart(chatId, resultResponse);
        LOGGER.info("Finished processing command without errors");
        return resultResponse;
    }
}
