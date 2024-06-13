package ru.liga.cargodistributor.bot.serviceImpls.cargoitemtype.creation;

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

public class Step3CargoItemTypeCreationCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(Step3CargoItemTypeCreationCommandHandlerService.class);

    public Step3CargoItemTypeCreationCommandHandlerService(CargoDistributorBotService botService) {
        super(botService);
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommand(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);
        String cargoItemTypeLegend = getMessageTextFromUpdate(update).strip();

        if (cargoItemTypeLegend.isBlank() || cargoItemTypeLegend.length() != 1) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.INCORRECT_CARGO_TYPE_LEGEND.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.ENTER_CARGO_TYPE_LEGEND.getMessageText()
                    )
            );
            LOGGER.info("Finished processing command, incorrect legend {}", cargoItemTypeLegend);
            return resultResponse;
        }

        botService.putCargoItemTypeLegendToCache(String.valueOf(chatId), cargoItemTypeLegend);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.SEND_FILE_WITH_SINGLE_CARGO.getMessageText()
                )
        );

        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
