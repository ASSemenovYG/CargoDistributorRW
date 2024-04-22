package ru.liga.cargodistributor.bot.commandhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.CargoDistributorBotKeyboard;
import ru.liga.cargodistributor.bot.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.CargoDistributorBotService;
import ru.liga.cargodistributor.cargo.CargoConverterService;
import ru.liga.cargodistributor.util.FileService;

import java.util.LinkedList;
import java.util.List;

@Service
public class ReadVanLimitCommandHandlerService extends CommandHandlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadVanLimitCommandHandlerService.class);

    @Autowired
    protected ReadVanLimitCommandHandlerService(@Value("${bot.token}") String token, @Value("${cache.capacity}") int cacheCapacity) {
        super(token, cacheCapacity);
    }

    public ReadVanLimitCommandHandlerService(TelegramClient telegramClient, CargoDistributorBotService botService, CargoConverterService cargoConverterService, FileService fileService) {
        super(telegramClient, botService, cargoConverterService, fileService);
    }

    @Override
    public List<Object> processCommandAndGetResponseMessages(Update update) {
        List<Object> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);

        int vanLimit;
        try {
            vanLimit = Integer.parseInt(getMessageTextFromUpdate(update));
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
                            CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_LIMIT.getMessageText()
                    )
            );

            return resultResponse;
        }
        botService.putVanLimitToCache(String.valueOf(chatId), vanLimit);

        resultResponse.add(
                botService.buildTextMessageWithKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.PICK_ALGORITHM.getMessageText(),
                        CargoDistributorBotKeyboard.PICK_ALGORITHM
                )
        );

        return resultResponse;
    }
}