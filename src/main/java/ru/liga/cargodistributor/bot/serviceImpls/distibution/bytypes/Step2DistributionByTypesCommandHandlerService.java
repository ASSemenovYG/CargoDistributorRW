package ru.liga.cargodistributor.bot.serviceImpls.distibution.bytypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.algorithm.CargoDistributionParameters;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.exceptions.RecordNotFoundException;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.CargoVan;
import ru.liga.cargodistributor.cargo.CargoVanType;
import ru.liga.cargodistributor.cargo.entity.CargoVanTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoVanTypeRepository;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.LinkedList;
import java.util.List;

public class Step2DistributionByTypesCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(Step2DistributionByTypesCommandHandlerService.class);

    private final CargoVanTypeRepository cargoVanTypeRepository;

    public Step2DistributionByTypesCommandHandlerService(
            TelegramClient telegramClient,
            CargoDistributorBotService botService,
            CargoConverterService cargoConverterService,
            FileService fileService,
            CargoVanTypeRepository cargoVanTypeRepository
    ) {
        super(telegramClient, botService, cargoConverterService, fileService);
        this.cargoVanTypeRepository = cargoVanTypeRepository;
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommandAndGetResponseMessages(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);
        String cargoVanTypeName = getMessageTextFromUpdate(update);

        CargoVanTypeInfo cargoVanTypeInfo;

        try {
            cargoVanTypeInfo = cargoVanTypeRepository
                    .findByName(cargoVanTypeName)
                    .orElseThrow(() -> new RecordNotFoundException("Couldn't find cargo van type with name: " + cargoVanTypeName));
        } catch (RecordNotFoundException e) {
            LOGGER.info(e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.CARGO_VAN_TYPE_TO_DISTRIBUTE_NOT_FOUND.getMessageText() + cargoVanTypeName
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
                            CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ENTER_CARGO_VAN_TYPE_NAME.getMessageText()
                    )
            );

            LOGGER.info("Finished processing command, cargo van type with name {} not found", cargoVanTypeName);
            return resultResponse;
        }

        CargoVan cargoVanType;

        try {
            cargoVanType = new CargoVanType(cargoVanTypeInfo);
        } catch (RuntimeException e) {
            LOGGER.error("Error occurred while creating cargo van from type with Id {}, errorMessage: {}", cargoVanTypeInfo.getId(), e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ERROR_WHILE_CREATING_CARGO_VAN.getMessageText()
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
                            CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ENTER_CARGO_VAN_TYPE_NAME.getMessageText()
                    )
            );

            LOGGER.info("Finished processing command, error occurred while creating cargo van");
            return resultResponse;
        }

        botService.putCargoDistributionParametersToCache(String.valueOf(chatId), new CargoDistributionParameters(cargoVanType));

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        cargoVanType.toString()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ENTER_CARGO_ITEM_TYPE_NAME.getMessageText()
                )
        );

        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
