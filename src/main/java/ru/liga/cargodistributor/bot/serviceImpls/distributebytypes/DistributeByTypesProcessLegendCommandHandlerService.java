package ru.liga.cargodistributor.bot.serviceImpls.distributebytypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.algorithm.CargoDistributionParameters;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.LinkedList;
import java.util.List;

public class DistributeByTypesProcessLegendCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributeByTypesProcessLegendCommandHandlerService.class);

    public DistributeByTypesProcessLegendCommandHandlerService(
            TelegramClient telegramClient,
            CargoDistributorBotService botService,
            CargoConverterService cargoConverterService,
            FileService fileService
    ) {
        super(telegramClient, botService, cargoConverterService, fileService);
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommandAndGetResponseMessages(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);

        CargoDistributionParameters cargoDistributionParameters = botService.getCargoDistributionParametersFromCache(String.valueOf(chatId));

        if (cargoDistributionParameters == null) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.FAILED_TO_FIND_CARGO_PARAMETERS_TO_DISTRIBUTE.getMessageText()
                    )
            );

            returnToStart(chatId, resultResponse);
            LOGGER.info("Finished processing command, cargo distribution parameters not found in cache");
            return resultResponse;
        }

        String cargoItemTypeLegend = getMessageTextFromUpdate(update).strip();
        //todo: проверку на VAN_BORDER_SYMBOL надо добавить в другие места, где можно указать легенду
        if (cargoItemTypeLegend.isBlank() || cargoItemTypeLegend.length() != 1 || cargoItemTypeLegend.equals(CargoConverterService.VAN_BORDER_SYMBOL)) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.INCORRECT_CARGO_TYPE_LEGEND.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_AVAILABLE_LEGEND_SYMBOLS.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_CARGO_ITEM_TYPE_WITH_SUCH_LEGEND_ALREADY_ADDED.getMessageText()
                    )
            );
            LOGGER.info("Finished processing command, incorrect legend {}", cargoItemTypeLegend);
            return resultResponse;
        }

        if (cargoDistributionParameters.isItemWithLegendAlreadyAdded(cargoItemTypeLegend)) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_AVAILABLE_LEGEND_SYMBOLS.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_CARGO_ITEM_TYPE_WITH_SUCH_LEGEND_ALREADY_ADDED.getMessageText()
                    )
            );
            LOGGER.info("Finished processing command, found duplicate legend");
            return resultResponse;
        }

        cargoDistributionParameters.getCargoItemsToLoad().getLast().getCargoItem().setLegend(cargoItemTypeLegend);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ENTER_CARGO_ITEM_TYPE_COUNT.getMessageText()
                )
        );

        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
