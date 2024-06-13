package ru.liga.cargodistributor.bot.serviceImpls.cargoitemtype.change;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoItemTypeRepository;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Step4_CargoItemTypeChangeCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(Step4_CargoItemTypeChangeCommandHandlerService.class);

    private final CargoItemTypeRepository cargoItemTypeRepository;

    public Step4_CargoItemTypeChangeCommandHandlerService(
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
    @Transactional
    public List<PartialBotApiMethod<Message>> processCommandAndGetResponseMessages(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);
        CargoItemTypeInfo cargoItemTypeInfoToUpdate = botService.getCargoItemTypeInfoToUpdateFromCache(String.valueOf(chatId));

        if (cargoItemTypeInfoToUpdate == null) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.EDIT_CARGO_TYPE_ITEM_NOT_FOUND_IN_CACHE.getMessageText()
                    )
            );

            returnToStart(chatId, resultResponse);
            LOGGER.info("Finished processing command, cargo item type to update not found in cache");
            return resultResponse;
        }

        UUID idToUpdate = cargoItemTypeInfoToUpdate.getId();
        cargoItemTypeRepository.save(cargoItemTypeInfoToUpdate);
        botService.putCargoItemTypeIntoToUpdateToCache(String.valueOf(chatId), null);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.CARGO_TYPE_SUCCESSFULLY_UPDATED.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        cargoItemTypeInfoToUpdate.toString()
                )
        );

        returnToStart(chatId, resultResponse);
        LOGGER.info("Finished processing command, cargo item type with id {} successfully updated", idToUpdate);
        return resultResponse;
    }
}
