package ru.liga.cargodistributor.bot.serviceImpls.editcargotype;

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
import ru.liga.cargodistributor.cargo.repository.CargoItemTypeRepository;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.LinkedList;
import java.util.List;

public class EditCargoTypeProcessNameCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(EditCargoTypeProcessNameCommandHandlerService.class);

    private final CargoItemTypeRepository cargoItemTypeRepository;

    public EditCargoTypeProcessNameCommandHandlerService(
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

        if (cargoItemTypeRepository.existsByName(cargoItemTypeName)) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.CARGO_TYPE_NAME_ALREADY_EXISTS.getMessageText()
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
            LOGGER.info("Finished processing command, cargo item type with name {} already exists", cargoItemTypeName);
            return resultResponse;
        }

        cargoItemTypeInfoToUpdate.setName(cargoItemTypeName);

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
