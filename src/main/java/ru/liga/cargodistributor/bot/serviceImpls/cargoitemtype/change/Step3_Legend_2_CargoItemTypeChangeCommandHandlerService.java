package ru.liga.cargodistributor.bot.serviceImpls.cargoitemtype.change;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotKeyboard;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.LinkedList;
import java.util.List;

public class Step3_Legend_2_CargoItemTypeChangeCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(Step3_Legend_2_CargoItemTypeChangeCommandHandlerService.class);

    public Step3_Legend_2_CargoItemTypeChangeCommandHandlerService(
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

        CargoItemTypeInfo cargoItemTypeInfoToUpdate = botService.getCargoItemTypeInfoToUpdateFromCache(String.valueOf(chatId));

        if (cargoItemTypeInfoToUpdate == null) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.FAILED_TO_FIND_CARGO_ITEM_TYPE_TO_UPDATE.getMessageText()
                    )
            );

            returnToStart(chatId, resultResponse);
            LOGGER.info("Finished processing command, cargo item type to update not found in cache");
            return resultResponse;
        }

        String cargoItemTypeLegend = getMessageTextFromUpdate(update).strip();
        if (cargoItemTypeLegend.isBlank() || cargoItemTypeLegend.length() != 1) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.INCORRECT_CARGO_TYPE_LEGEND.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.ENTER_NEW_CARGO_TYPE_LEGEND.getMessageText()
                    )
            );
            LOGGER.info("Finished processing command, incorrect legend {}", cargoItemTypeLegend);
            return resultResponse;
        }

        cargoItemTypeInfoToUpdate.setLegend(cargoItemTypeLegend);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.UPDATE_CARGO_TYPE_CURRENT_PARAMETERS.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        cargoItemTypeInfoToUpdate.toString()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.EDIT_CARGO_TYPE_PICK_PARAMETER.getMessageText(),
                        CargoDistributorBotKeyboard.EDIT_CARGO_TYPE
                )
        );

        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
