package ru.liga.cargodistributor.bot.serviceImpls.cargovantype.change;

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
import ru.liga.cargodistributor.cargo.entity.CargoVanTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoVanTypeRepository;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.LinkedList;
import java.util.List;

public class Step3_Name_2_CargoVanTypeChangeCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(Step3_Name_2_CargoVanTypeChangeCommandHandlerService.class);

    private final CargoVanTypeRepository cargoVanTypeRepository;

    public Step3_Name_2_CargoVanTypeChangeCommandHandlerService(
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

        CargoVanTypeInfo cargoVanTypeInfoToUpdate = botService.getCargoVanTypeInfoFromCache(String.valueOf(chatId));

        if (cargoVanTypeInfoToUpdate == null) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.FAILED_TO_FIND_CARGO_VAN_TYPE_TO_UPDATE.getMessageText()
                    )
            );

            returnToStart(chatId, resultResponse);
            LOGGER.info("Finished processing command, cargo van type to update not found in cache");
            return resultResponse;
        }

        if (cargoVanTypeRepository.existsByName(cargoVanTypeName)) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.CARGO_VAN_TYPE_NAME_ALREADY_EXISTS.getMessageText()
                    )
            );

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
            LOGGER.info("Finished processing command, cargo van type with name {} already exists", cargoVanTypeName);
            return resultResponse;
        }

        cargoVanTypeInfoToUpdate.setName(cargoVanTypeName);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.UPDATE_CARGO_VAN_TYPE_CURRENT_PARAMETERS.getMessageText()
                )
        );

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
