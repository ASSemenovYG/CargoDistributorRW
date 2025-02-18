package ru.liga.cargodistributor.bot.serviceImpls.cargovantype.change;

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
import ru.liga.cargodistributor.cargo.entity.CargoVanTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoVanTypeRepository;

import java.util.LinkedList;
import java.util.List;

public class Step2_CargoVanTypeChangeCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(Step2_CargoVanTypeChangeCommandHandlerService.class);

    private final CargoVanTypeRepository cargoVanTypeRepository;

    public Step2_CargoVanTypeChangeCommandHandlerService(
            CargoDistributorBotService botService,
            CargoVanTypeRepository cargoVanTypeRepository
    ) {
        super(botService);
        this.cargoVanTypeRepository = cargoVanTypeRepository;
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommand(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);
        String cargoVanTypeName = getMessageTextFromUpdate(update);

        CargoVanTypeInfo cargoVanTypeInfoToUpdate;

        try {
            cargoVanTypeInfoToUpdate = cargoVanTypeRepository
                    .findByName(cargoVanTypeName)
                    .orElseThrow(() -> new RecordNotFoundException("Couldn't find cargo van type with name: " + cargoVanTypeName));
        } catch (RecordNotFoundException e) {
            LOGGER.info(e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.CARGO_VAN_TYPE_TO_EDIT_NOT_FOUND.getMessageText() + cargoVanTypeName
                    )
            );

            returnToStart(chatId, resultResponse);

            LOGGER.info("Finished processing command, cargo van type with name {} not found", cargoVanTypeName);
            return resultResponse;
        }

        botService.putCargoVanTypeInfoToCache(String.valueOf(chatId), cargoVanTypeInfoToUpdate);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        cargoVanTypeInfoToUpdate.toString()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.EDIT_CARGO_VAN_TYPE_PICK_PARAMETER.getMessageText(),
                        CargoDistributorBotKeyboard.EDIT_CARGO_VAN_TYPE
                )
        );

        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
