package ru.liga.cargodistributor.bot.commandhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.CargoDistributorBotService;
import ru.liga.cargodistributor.cargo.CargoConverterService;
import ru.liga.cargodistributor.util.FileService;

import java.util.LinkedList;
import java.util.List;

@Service
public class FileReadErrorUnknownCommandHandlerService extends CommandHandlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileReadErrorUnknownCommandHandlerService.class);
    private final String errorMessage;

    @Autowired
    protected FileReadErrorUnknownCommandHandlerService(@Value("${bot.token}") String token, @Value("${cache.capacity}") int cacheCapacity) {
        super(token, cacheCapacity);
        this.errorMessage = null;
    }

    public FileReadErrorUnknownCommandHandlerService(
            TelegramClient telegramClient,
            CargoDistributorBotService botService,
            CargoConverterService cargoConverterService,
            FileService fileService,
            String errorMessage
    ) {
        super(telegramClient, botService, cargoConverterService, fileService);
        this.errorMessage = errorMessage;
    }

    @Override
    public List<Object> processCommandAndGetResponseMessages(Update update) {
        LOGGER.info("Started processing command");

        List<Object> resultResponse = new LinkedList<>();
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
