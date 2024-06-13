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

public class Step1CargoLoadReaderCommandHandlerService extends CommandHandlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Step1CargoLoadReaderCommandHandlerService.class);

    public Step1CargoLoadReaderCommandHandlerService(CargoDistributorBotService botService) {
        super(botService);
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommandAndGetResponseMessages(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        getChatIdFromUpdate(update),
                        CargoDistributorBotResponseMessage.SEND_LOADED_VANS_TO_READ.getMessageText()
                )
        );
        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
