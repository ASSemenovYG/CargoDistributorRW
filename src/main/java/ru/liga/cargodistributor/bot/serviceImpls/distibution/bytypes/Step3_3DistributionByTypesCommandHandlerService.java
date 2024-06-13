package ru.liga.cargodistributor.bot.serviceImpls.distibution.bytypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.algorithm.CargoDistributionParameters;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotKeyboard;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;

import java.util.LinkedList;
import java.util.List;

public class Step3_3DistributionByTypesCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(Step3_3DistributionByTypesCommandHandlerService.class);

    public Step3_3DistributionByTypesCommandHandlerService(CargoDistributorBotService botService) {
        super(botService);
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommand(Update update) {
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

        int cargoItemTypeCount;
        try {
            cargoItemTypeCount = Integer.parseInt(getMessageTextFromUpdate(update));
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.FAILED_TO_PARSE_INTEGER.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.TRY_AGAIN.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ENTER_CARGO_ITEM_TYPE_COUNT.getMessageText()
                    )
            );

            LOGGER.info("Finished processing command, error occurred while parsing Integer");
            return resultResponse;
        }

        if (cargoItemTypeCount < 1) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.NEED_TO_ENTER_INTEGER_GREATER_THAN_ZERO.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.TRY_AGAIN.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ENTER_CARGO_ITEM_TYPE_COUNT.getMessageText()
                    )
            );

            LOGGER.info("Finished processing command, user entered invalid count: {}", cargoItemTypeCount);
            return resultResponse;
        }


        LinkedList<CargoDistributionParameters.CargoItemToLoad> cargoItemsToLoad = cargoDistributionParameters.getCargoItemsToLoad();
        cargoItemsToLoad.getLast().setCount(cargoItemTypeCount);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_CURRENT_CARGO_ITEM_TYPE_LIST.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        cargoDistributionParameters.getCargoItemsToLoadStringDescription()
                )
        );


        resultResponse.add(
                botService.buildTextMessageWithKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ADD_MORE_CARGO_TYPE_OR_CONTINUE.getMessageText(),
                        CargoDistributorBotKeyboard.DISTRIBUTE_BY_TYPES_ADD_MORE_CARGO_OR_CONTINUE
                )
        );

        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
