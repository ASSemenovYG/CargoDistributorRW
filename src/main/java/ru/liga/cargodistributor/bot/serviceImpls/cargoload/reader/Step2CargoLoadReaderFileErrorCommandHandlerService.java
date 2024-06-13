package ru.liga.cargodistributor.bot.serviceImpls.cargoload.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;

import java.util.LinkedList;
import java.util.List;

public class Step2CargoLoadReaderFileErrorCommandHandlerService extends CommandHandlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Step2CargoLoadReaderFileErrorCommandHandlerService.class);

    private final String errorMessage;

    public Step2CargoLoadReaderFileErrorCommandHandlerService(
            CargoDistributorBotService botService,
            String errorMessage
    ) {
        super(botService);
        this.errorMessage = errorMessage;
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommand(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.ERROR_WHILE_PROCESSING_CARGO_VAN_FILE.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        "```" + errorMessage + "```"
                )
        );

        returnToStart(chatId, resultResponse);
        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
