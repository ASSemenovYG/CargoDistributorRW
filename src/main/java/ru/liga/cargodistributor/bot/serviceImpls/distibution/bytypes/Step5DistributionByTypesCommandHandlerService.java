package ru.liga.cargodistributor.bot.serviceImpls.distibution.bytypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.algorithm.CargoDistributionParameters;
import ru.liga.cargodistributor.algorithm.enums.DistributionAlgorithmName;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotKeyboard;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.LinkedList;
import java.util.List;

public class Step5DistributionByTypesCommandHandlerService extends CommandHandlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Step5DistributionByTypesCommandHandlerService.class);

    public Step5DistributionByTypesCommandHandlerService(
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

        CargoDistributionParameters cargoDistributionParameters = botService.getCargoDistributionParametersFromCache(String.valueOf(chatId));

        if (cargoDistributionParameters == null) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.FAILED_TO_FIND_CARGO_PARAMETERS_TO_DISTRIBUTE.getMessageText()
                    )
            );

            returnToStart(chatId, resultResponse);
            LOGGER.info("Finished processing command, cargo distribution parameters not found in cache");
            return resultResponse;
        }

        LOGGER.info("trying to resolve algorithm name from text message: {}", getMessageTextFromUpdate(update));
        DistributionAlgorithmName algorithmName = DistributionAlgorithmName.fromString(getMessageTextFromUpdate(update));

        try {
            if (algorithmName == null) {
                throw new RuntimeException("algorithmName cannot be null");
            }
        } catch (RuntimeException e) {
            LOGGER.error("couldn't resolve algorithm: {}", e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.CANT_RESOLVE_PICKED_ALGORITHM_NAME.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_PICK_ALGORITHM.getMessageText(),
                            CargoDistributorBotKeyboard.PICK_ALGORITHM
                    )
            );
            LOGGER.info("Finished processing command, couldn't resolve algorithm");
            return resultResponse;
        }

        cargoDistributionParameters.setAlgorithmName(algorithmName);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ENTER_VAN_LIMIT.getMessageText()
                )
        );

        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
