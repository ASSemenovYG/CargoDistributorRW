package ru.liga.cargodistributor.bot.serviceImpls.cargoitemtype.change;

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

public class Step3_Name_1_CargoItemTypeChangeCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(Step3_Name_1_CargoItemTypeChangeCommandHandlerService.class);

    public Step3_Name_1_CargoItemTypeChangeCommandHandlerService(CargoDistributorBotService botService) {
        super(botService);
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommand(Update update) {
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
