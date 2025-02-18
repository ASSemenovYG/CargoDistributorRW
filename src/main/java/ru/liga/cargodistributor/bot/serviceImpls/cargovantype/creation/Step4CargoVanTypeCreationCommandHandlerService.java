package ru.liga.cargodistributor.bot.serviceImpls.cargovantype.creation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.entity.CargoVanTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoVanTypeRepository;

import java.util.LinkedList;
import java.util.List;

public class Step4CargoVanTypeCreationCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(Step4CargoVanTypeCreationCommandHandlerService.class);

    private final CargoVanTypeRepository cargoVanTypeRepository;

    public Step4CargoVanTypeCreationCommandHandlerService(
            CargoDistributorBotService botService,
            CargoVanTypeRepository cargoVanTypeRepository
    ) {
        super(botService);
        this.cargoVanTypeRepository = cargoVanTypeRepository;
    }

    @Override
    @Transactional
    public List<PartialBotApiMethod<Message>> processCommand(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);

        int vanLength;
        try {
            vanLength = Integer.parseInt(getMessageTextFromUpdate(update));
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
                            CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_TYPE_LENGTH.getMessageText()
                    )
            );

            LOGGER.info("Finished processing command, error occurred while parsing Integer");
            return resultResponse;
        }

        if (vanLength < 1) {
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
                            CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_TYPE_LENGTH.getMessageText()
                    )
            );

            LOGGER.info("Finished processing command, user entered invalid length: {}", vanLength);
            return resultResponse;
        }

        CargoVanTypeInfo cargoVanTypeInfoToInsert = botService.getCargoVanTypeInfoFromCache(String.valueOf(chatId));

        if (cargoVanTypeInfoToInsert == null) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.FAILED_TO_FIND_CARGO_VAN_TYPE_TO_INSERT.getMessageText()
                    )
            );

            returnToStart(chatId, resultResponse);
            LOGGER.info("Finished processing command, cargo van type to insert not found in cache");
            return resultResponse;
        }

        cargoVanTypeInfoToInsert.setLength(vanLength);

        cargoVanTypeRepository.save(cargoVanTypeInfoToInsert);
        String cargoVanTypeName = cargoVanTypeInfoToInsert.getName();
        botService.putCargoVanTypeInfoToCache(String.valueOf(chatId), null);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.CARGO_VAN_TYPE_SUCCESSFULLY_ADDED.getMessageText() + cargoVanTypeName
                )
        );

        returnToStart(chatId, resultResponse);

        LOGGER.info("Finished processing command, cargo van type with ID {} successfully added", cargoVanTypeInfoToInsert.getId());
        return resultResponse;
    }
}
