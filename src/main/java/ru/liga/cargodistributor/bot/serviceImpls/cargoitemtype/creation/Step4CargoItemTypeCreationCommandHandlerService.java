package ru.liga.cargodistributor.bot.serviceImpls.cargoitemtype.creation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoItemTypeRepository;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;

import java.util.LinkedList;
import java.util.List;

public class Step4CargoItemTypeCreationCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(Step4CargoItemTypeCreationCommandHandlerService.class);

    private final CargoItemTypeRepository cargoItemTypeRepository;
    private final CargoConverterService cargoConverterService;
    private final String cargoContent;

    public Step4CargoItemTypeCreationCommandHandlerService(
            CargoDistributorBotService botService,
            CargoConverterService cargoConverterService,
            CargoItemTypeRepository cargoItemTypeRepository,
            String cargoContent
    ) {
        super(botService);
        this.cargoItemTypeRepository = cargoItemTypeRepository;
        this.cargoConverterService = cargoConverterService;
        this.cargoContent = cargoContent;
    }

    @Override
    @Transactional
    public List<PartialBotApiMethod<Message>> processCommand(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);

        CargoItemList cargoList;
        try {
            cargoList = new CargoItemList(cargoConverterService.parseCargoItems(cargoContent));
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.ERROR_WHILE_PROCESSING_CARGO_FILE.getMessageText()
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
                            CargoDistributorBotResponseMessage.SEND_FILE_WITH_SINGLE_CARGO.getMessageText()
                    )
            );
            LOGGER.info("Finished processing command, error occurred while processing file with single cargo");
            return resultResponse;
        }

        if (cargoList.isEmptyOrNull()) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.NO_CARGO_ITEMS_FOUND_IN_A_FILE.getMessageText()
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
                            CargoDistributorBotResponseMessage.SEND_FILE_WITH_SINGLE_CARGO.getMessageText()
                    )
            );
            LOGGER.info("Finished processing command, cargo list is empty");
            return resultResponse;
        }

        if (cargoList.getCargo().size() > 1) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.MORE_THAN_ONE_CARGO_ITEM_FOUND_IN_A_FILE.getMessageText()
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
                            CargoDistributorBotResponseMessage.SEND_FILE_WITH_SINGLE_CARGO.getMessageText()
                    )
            );
            LOGGER.info("Finished processing command, cargo items found in a file {}", cargoList.getCargo().size());
            return resultResponse;
        }

        String cargoItemTypeName = botService.getCargoItemTypeNameFromCache(String.valueOf(chatId));
        String cargoItemTypeLegend = botService.getCargoItemTypeLegendFromCache(String.valueOf(chatId));
        if (cargoItemTypeName == null || cargoItemTypeLegend == null) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.FAILED_TO_FIND_CARGO_ITEM_TYPE_DATA.getMessageText()
                    )
            );

            returnToStart(chatId, resultResponse);
            return resultResponse;
        }

        CargoItemTypeInfo cargoItemTypeInfoToInsert = new CargoItemTypeInfo(
                cargoItemTypeName,
                cargoItemTypeLegend,
                cargoList.getCargo().get(0).getName()
        );

        cargoItemTypeRepository.save(cargoItemTypeInfoToInsert);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.CARGO_ITEM_TYPE_SUCCESSFULLY_ADDED.getMessageText() + cargoItemTypeName
                )
        );

        returnToStart(chatId, resultResponse);

        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
