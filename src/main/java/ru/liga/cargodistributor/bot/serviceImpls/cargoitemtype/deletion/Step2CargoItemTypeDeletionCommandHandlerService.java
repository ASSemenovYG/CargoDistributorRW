package ru.liga.cargodistributor.bot.serviceImpls.cargoitemtype.deletion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.exceptions.RecordNotFoundException;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoItemTypeRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Step2CargoItemTypeDeletionCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(Step2CargoItemTypeDeletionCommandHandlerService.class);

    private final CargoItemTypeRepository cargoItemTypeRepository;

    public Step2CargoItemTypeDeletionCommandHandlerService(
            CargoDistributorBotService botService,
            CargoItemTypeRepository cargoItemTypeRepository
    ) {
        super(botService);
        this.cargoItemTypeRepository = cargoItemTypeRepository;
    }

    @Override
    @Transactional
    public List<PartialBotApiMethod<Message>> processCommandAndGetResponseMessages(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);
        String cargoItemTypeName = getMessageTextFromUpdate(update);
        CargoItemTypeInfo cargoItemTypeInfoToDelete;

        try {
            cargoItemTypeInfoToDelete = cargoItemTypeRepository
                    .findByName(cargoItemTypeName)
                    .orElseThrow(() -> new RecordNotFoundException("Couldn't find cargo item type with name: " + cargoItemTypeName));
        } catch (RecordNotFoundException e) {
            LOGGER.info(e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.CARGO_TYPE_TO_DELETE_NOT_FOUND.getMessageText() + cargoItemTypeName
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.ENTER_CARGO_TYPE_NAME_TO_DELETE.getMessageText()
                    )
            );

            LOGGER.info("Finished processing command, cargo item type with name {} not found", cargoItemTypeName);
            return resultResponse;
        }

        UUID idToDelete = cargoItemTypeInfoToDelete.getId();
        cargoItemTypeRepository.delete(cargoItemTypeInfoToDelete);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.CARGO_TYPE_SUCCESSFULLY_DELETED.getMessageText() + cargoItemTypeName
                )
        );

        returnToStart(chatId, resultResponse);
        LOGGER.info("Finished processing command, cargo item type with id {} successfully deleted", idToDelete);
        return resultResponse;
    }
}
