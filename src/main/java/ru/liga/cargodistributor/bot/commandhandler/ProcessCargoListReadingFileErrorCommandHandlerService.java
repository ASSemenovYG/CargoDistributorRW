package ru.liga.cargodistributor.bot.commandhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.CargoDistributorBotService;
import ru.liga.cargodistributor.cargo.CargoConverterService;
import ru.liga.cargodistributor.util.FileService;

import java.util.LinkedList;
import java.util.List;

public class ProcessCargoListReadingFileErrorCommandHandlerService extends CommandHandlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessCargoListReadingFileErrorCommandHandlerService.class);
    private final String errorMessage;

    @Autowired
    protected ProcessCargoListReadingFileErrorCommandHandlerService(@Value("${bot.token}") String token, @Value("${cache.capacity}") int cacheCapacity) {
        super(token, cacheCapacity);
        this.errorMessage = null;
    }

    public ProcessCargoListReadingFileErrorCommandHandlerService(
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
                        CargoDistributorBotResponseMessage.ERROR_WHILE_PROCESSING_CARGO_FILE.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        "```" + errorMessage + "```"
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
                        CargoDistributorBotResponseMessage.SEND_FILE_WITH_CARGO.getMessageText()
                )
        );
        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
