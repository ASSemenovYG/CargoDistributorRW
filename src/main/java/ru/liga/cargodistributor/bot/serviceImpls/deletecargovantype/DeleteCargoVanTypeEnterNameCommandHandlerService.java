package ru.liga.cargodistributor.bot.serviceImpls.deletecargovantype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.exceptions.RecordNotFoundException;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.entity.CargoVanTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoVanTypeRepository;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class DeleteCargoVanTypeEnterNameCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteCargoVanTypeEnterNameCommandHandlerService.class);

    private final CargoVanTypeRepository cargoVanTypeRepository;

    public DeleteCargoVanTypeEnterNameCommandHandlerService(
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
    @Transactional
    public List<PartialBotApiMethod<Message>> processCommandAndGetResponseMessages(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);
        String cargoVanTypeName = getMessageTextFromUpdate(update);
        CargoVanTypeInfo cargoVanTypeInfoToDelete;

        try {
            cargoVanTypeInfoToDelete = cargoVanTypeRepository
                    .findByName(cargoVanTypeName)
                    .orElseThrow(() -> new RecordNotFoundException("Couldn't find cargo van type with name: " + cargoVanTypeName));
        } catch (RecordNotFoundException e) {
            LOGGER.info(e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.CARGO_VAN_TYPE_TO_DELETE_NOT_FOUND.getMessageText() + cargoVanTypeName
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_TYPE_NAME_TO_DELETE.getMessageText()
                    )
            );

            LOGGER.info("Finished processing command, cargo van type with name {} not found", cargoVanTypeName);
            return resultResponse;
        }

        UUID idToDelete = cargoVanTypeInfoToDelete.getId();
        cargoVanTypeRepository.delete(cargoVanTypeInfoToDelete);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.CARGO_VAN_TYPE_SUCCESSFULLY_DELETED.getMessageText() + cargoVanTypeName
                )
        );

        returnToStart(chatId, resultResponse);
        LOGGER.info("Finished processing command, cargo van type with id {} successfully deleted", idToDelete);
        return resultResponse;
    }
}
