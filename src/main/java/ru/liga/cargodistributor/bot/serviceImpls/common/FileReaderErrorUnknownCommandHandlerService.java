package ru.liga.cargodistributor.bot.serviceImpls.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;

import java.util.LinkedList;
import java.util.List;

public class FileReaderErrorUnknownCommandHandlerService extends CommandHandlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileReaderErrorUnknownCommandHandlerService.class);
    private final String errorMessage;

    public FileReaderErrorUnknownCommandHandlerService(
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
                        CargoDistributorBotResponseMessage.ERROR_WHILE_READING_FROM_FILE_MESSAGE.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        "```" + errorMessage + "```"
                )
        );

        SendMessage lastMessage = botService.getLastSendMessageFromCache(String.valueOf(chatId));

        if (lastMessage != null) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.ERROR_WHILE_READING_FROM_FILE_FOUND_PREVIOUS_RESPONSE.getMessageText()
                    )
            );

            resultResponse.add(lastMessage);
        }

        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
