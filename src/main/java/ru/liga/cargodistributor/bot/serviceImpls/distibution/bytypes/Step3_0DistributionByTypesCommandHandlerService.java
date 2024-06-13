package ru.liga.cargodistributor.bot.serviceImpls.distibution.bytypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.algorithm.CargoDistributionParameters;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.exceptions.RecordNotFoundException;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.CargoItem;
import ru.liga.cargodistributor.cargo.CargoItemType;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoItemTypeRepository;

import java.util.LinkedList;
import java.util.List;

public class Step3_0DistributionByTypesCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(Step3_0DistributionByTypesCommandHandlerService.class);

    private final CargoItemTypeRepository cargoItemTypeRepository;

    public Step3_0DistributionByTypesCommandHandlerService(
            CargoDistributorBotService botService,
            CargoItemTypeRepository cargoItemTypeRepository
    ) {
        super(botService);
        this.cargoItemTypeRepository = cargoItemTypeRepository;
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommandAndGetResponseMessages(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);
        String cargoItemTypeName = getMessageTextFromUpdate(update);

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

        CargoItemTypeInfo cargoItemTypeInfo;

        try {
            cargoItemTypeInfo = cargoItemTypeRepository
                    .findByName(cargoItemTypeName)
                    .orElseThrow(() -> new RecordNotFoundException("Couldn't find cargo item type with name: " + cargoItemTypeName));
        } catch (RecordNotFoundException e) {
            LOGGER.info(e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.CARGO_ITEM_TYPE_TO_DISTRIBUTE_NOT_FOUND.getMessageText() + cargoItemTypeName
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
                            CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ENTER_CARGO_ITEM_TYPE_NAME.getMessageText()
                    )
            );

            LOGGER.info("Finished processing command, cargo item type with name {} not found", cargoItemTypeName);
            return resultResponse;
        }

        CargoItem cargoItemType;

        try {
            cargoItemType = new CargoItemType(cargoItemTypeInfo, cargoDistributionParameters.getCargoVan());
        } catch (RuntimeException e) {
            LOGGER.error("Error occurred while creating cargo item from type with Id {}, errorMessage: {}", cargoItemTypeInfo.getId(), e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ERROR_WHILE_CREATING_CARGO_ITEM.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            "```" + e.getMessage() + "```"
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
                            CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ENTER_CARGO_ITEM_TYPE_NAME.getMessageText()
                    )
            );

            LOGGER.info("Finished processing command, error occurred while creating cargo item");
            return resultResponse;
        }

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_CARGO_ITEM_TYPE_ADDED.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        cargoItemTypeInfo.toString()
                )
        );

        if (cargoDistributionParameters.isItemWithLegendAlreadyAdded(cargoItemType.getLegend())) {
            cargoDistributionParameters.addCargoItemToLoad(new CargoDistributionParameters.CargoItemToLoad(cargoItemType, 1));

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

        cargoDistributionParameters.addCargoItemToLoad(new CargoDistributionParameters.CargoItemToLoad(cargoItemType, 1));

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
