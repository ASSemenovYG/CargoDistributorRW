package ru.liga.cargodistributor.bot.serviceImpls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotKeyboard;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.entity.CargoVanTypeInfo;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.LinkedList;
import java.util.List;

@Service
public class EditCargoVanTypeProcessWidthCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(EditCargoVanTypeProcessWidthCommandHandlerService.class);

    @Autowired
    protected EditCargoVanTypeProcessWidthCommandHandlerService(@Value("${bot.token}") String token, @Value("${cache.capacity}") int cacheCapacity) {
        super(token, cacheCapacity);
    }

    public EditCargoVanTypeProcessWidthCommandHandlerService(
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

        int vanWidth;
        try {
            vanWidth = Integer.parseInt(getMessageTextFromUpdate(update));
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.FAILED_TO_PARSE_INTEGER.getMessageText()
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
                            CargoDistributorBotResponseMessage.ENTER_NEW_CARGO_VAN_TYPE_WIDTH.getMessageText()
                    )
            );

            LOGGER.info("Finished processing command, error occurred while parsing Integer");
            return resultResponse;
        }

        if (vanWidth < 1) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.NEED_TO_ENTER_INTEGER_GREATER_THAN_ZERO.getMessageText()
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
                            CargoDistributorBotResponseMessage.ENTER_NEW_CARGO_VAN_TYPE_WIDTH.getMessageText()
                    )
            );

            LOGGER.info("Finished processing command, user entered invalid width: {}", vanWidth);
            return resultResponse;
        }

        cargoVanTypeInfoToUpdate.setWidth(vanWidth);

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
