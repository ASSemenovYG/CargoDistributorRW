package ru.liga.cargodistributor.bot.commandhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.CargoDistributorBotService;
import ru.liga.cargodistributor.cargo.CargoConverterService;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.util.FileService;

import java.util.LinkedList;
import java.util.List;

@Service
public class ProcessCargoListCommandHandlerService extends CommandHandlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessCargoListCommandHandlerService.class);

    @Autowired
    protected ProcessCargoListCommandHandlerService(@Value("${bot.token}") String token, @Value("${cache.capacity}") int cacheCapacity) {
        super(token, cacheCapacity);
    }

    public ProcessCargoListCommandHandlerService(TelegramClient telegramClient, CargoDistributorBotService botService, CargoConverterService cargoConverterService, FileService fileService) {
        super(telegramClient, botService, cargoConverterService, fileService);
    }

    @Override
    public List<Object> processCommandAndGetResponseMessages(Update update) {
        LOGGER.info("Started processing command");
        List<Object> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);

        CargoItemList cargoList;
        try {
            cargoList = new CargoItemList(
                    cargoConverterService.parseCargoItems(
                            fileService.readFromFile(
                                    botService.getFileFromUpdate(update, telegramClient)
                            )
                    )
            );
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.ERROR_WHILE_PROCESSING_CARGO_FILE.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            "```" + e.getMessage() + "```"
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
            LOGGER.info("Finished processing command, error occurred while processing file with cargo list");
            return resultResponse;
        }

        if (cargoList.isEmptyOrNull()) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.NO_CARGO_ITEMS_FOUND_IN_A_FILE.getMessageText()
                    )
            );

            returnToStart(chatId, resultResponse);
            LOGGER.info("Finished processing command, cargo list is empty");
            return resultResponse;
        }

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.FOUND_CARGO_ITEMS_IN_A_FILE.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        "```" + cargoList.getCargoItemNamesAsString() + "```"
                )
        );

        botService.putCargoItemListToCache(String.valueOf(chatId), cargoList);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_LIMIT.getMessageText()
                )
        );

        LOGGER.info("Finished processing command, cargo list saved to cache");
        return resultResponse;
    }
}
