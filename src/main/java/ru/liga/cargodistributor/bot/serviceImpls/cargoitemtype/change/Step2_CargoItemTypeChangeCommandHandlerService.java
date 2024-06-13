package ru.liga.cargodistributor.bot.serviceImpls.cargoitemtype.change;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotKeyboard;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.exceptions.RecordNotFoundException;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoItemTypeRepository;

import java.util.LinkedList;
import java.util.List;

public class Step2_CargoItemTypeChangeCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(Step2_CargoItemTypeChangeCommandHandlerService.class);

    private final CargoItemTypeRepository cargoItemTypeRepository;

    public Step2_CargoItemTypeChangeCommandHandlerService(
            CargoDistributorBotService botService,
            CargoItemTypeRepository cargoItemTypeRepository
    ) {
        super(botService);
        this.cargoItemTypeRepository = cargoItemTypeRepository;
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommand(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);
        String cargoItemTypeName = getMessageTextFromUpdate(update);

        CargoItemTypeInfo cargoItemTypeInfoToUpdate;

        try {
            cargoItemTypeInfoToUpdate = cargoItemTypeRepository
                    .findByName(cargoItemTypeName)
                    .orElseThrow(() -> new RecordNotFoundException("Couldn't find cargo item type with name: " + cargoItemTypeName));
        } catch (RecordNotFoundException e) {
            LOGGER.info(e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.CARGO_TYPE_TO_EDIT_NOT_FOUND.getMessageText() + cargoItemTypeName
                    )
            );

            returnToStart(chatId, resultResponse);

            LOGGER.info("Finished processing command, cargo item type with name {} not found", cargoItemTypeName);
            return resultResponse;
        }

        botService.putCargoItemTypeIntoToUpdateToCache(String.valueOf(chatId), cargoItemTypeInfoToUpdate);

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
