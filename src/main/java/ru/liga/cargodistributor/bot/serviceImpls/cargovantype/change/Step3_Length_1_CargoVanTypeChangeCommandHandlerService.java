package ru.liga.cargodistributor.bot.serviceImpls.cargovantype.change;

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

public class Step3_Length_1_CargoVanTypeChangeCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(Step3_Length_1_CargoVanTypeChangeCommandHandlerService.class);

    public Step3_Length_1_CargoVanTypeChangeCommandHandlerService(CargoDistributorBotService botService) {
        super(botService);
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommand(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        getChatIdFromUpdate(update),
                        CargoDistributorBotResponseMessage.ENTER_NEW_CARGO_VAN_TYPE_LENGTH.getMessageText()
                )
        );

        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
