package ru.liga.cargodistributor.bot.serviceImpls.editcargotype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.LinkedList;
import java.util.List;

public class EditCargoTypeEnterNameCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(EditCargoTypeEnterNameCommandHandlerService.class);

    public EditCargoTypeEnterNameCommandHandlerService(
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

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        getChatIdFromUpdate(update),
                        CargoDistributorBotResponseMessage.ENTER_NEW_CARGO_TYPE_NAME.getMessageText()
                )
        );

        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
