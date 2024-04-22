package ru.liga.cargodistributor.bot.commandhandler;

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
public class StartCommandHandlerService extends CommandHandlerService {
    @Autowired
    protected StartCommandHandlerService(@Value("${bot.token}") String token, @Value("${cache.capacity}") int cacheCapacity) {
        super(token, cacheCapacity);
    }

    public StartCommandHandlerService(TelegramClient telegramClient, CargoDistributorBotService botService, CargoConverterService cargoConverterService, FileService fileService) {
        super(telegramClient, botService, cargoConverterService, fileService);
    }

    @Override
    public List<Object> processCommandAndGetResponseMessages(Update update) {
        List<Object> resultResponse = new LinkedList<>();
        resultResponse.add(
                botService.buildTextMessageWithKeyboard(
                        getChatIdFromUpdate(update),
                        CargoDistributorBotResponseMessage.LOOK_WHAT_I_CAN_DO.getMessageText(),
                        CargoDistributorBotKeyboard.START
                )
        );
        return resultResponse;
    }
}