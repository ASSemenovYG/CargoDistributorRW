package ru.liga.cargodistributor.bot.serviceImpls.cargoload.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.CargoVanList;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.LinkedList;
import java.util.List;

public class Step2CargoLoadReaderCommandHandlerService extends CommandHandlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Step2CargoLoadReaderCommandHandlerService.class);

    private final String jsonContent;

    public Step2CargoLoadReaderCommandHandlerService(
            TelegramClient telegramClient,
            CargoDistributorBotService botService,
            CargoConverterService cargoConverterService,
            FileService fileService,
            String jsonContent
    ) {
        super(telegramClient, botService, cargoConverterService, fileService);
        this.jsonContent = jsonContent;
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommandAndGetResponseMessages(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);

        CargoVanList cargoVanList;
        LOGGER.info("Reading JSON with cargo vans");
        try {
            cargoVanList = cargoConverterService.deserializeLoadedVansFromJson(
                    (
                            jsonContent == null || jsonContent.isEmpty() || jsonContent.isBlank()
                                    ? update.getMessage().getText()
                                    : jsonContent
                    )
            );
        } catch (RuntimeException e) {
            LOGGER.error("error while deserializing JSON: {}", e.getMessage());

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
            LOGGER.info("Finished processing command, error occurred while deserializing JSON");
            return resultResponse;
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
