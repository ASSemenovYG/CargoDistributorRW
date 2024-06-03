package ru.liga.cargodistributor.bot.serviceImpls.distributebytypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.algorithm.CargoDistributionParameters;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.exceptions.RecordNotFoundException;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.CargoItem;
import ru.liga.cargodistributor.cargo.CargoItemType;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoItemTypeRepository;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.LinkedList;
import java.util.List;

@Service
public class DistributeByTypesProcessCargoTypeNameCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributeByTypesProcessCargoTypeNameCommandHandlerService.class);

    private final CargoItemTypeRepository cargoItemTypeRepository;

    @Autowired
    protected DistributeByTypesProcessCargoTypeNameCommandHandlerService(@Value("${bot.token}") String token, @Value("${cache.capacity}") int cacheCapacity, CargoItemTypeRepository cargoItemTypeRepository) {
        super(token, cacheCapacity);
        this.cargoItemTypeRepository = cargoItemTypeRepository;
    }

    public DistributeByTypesProcessCargoTypeNameCommandHandlerService(
            TelegramClient telegramClient,
            CargoDistributorBotService botService,
            CargoConverterService cargoConverterService,
            FileService fileService,
            CargoItemTypeRepository cargoItemTypeRepository
    ) {
        super(telegramClient, botService, cargoConverterService, fileService);
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

        cargoDistributionParameters.addCargoItemToLoad(new CargoDistributionParameters.CargoItemToLoad(cargoItemType, 1));

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
