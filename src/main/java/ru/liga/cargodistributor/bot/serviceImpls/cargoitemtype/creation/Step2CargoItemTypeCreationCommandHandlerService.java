package ru.liga.cargodistributor.bot.serviceImpls.cargoitemtype.creation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.repository.CargoItemTypeRepository;

import java.util.LinkedList;
import java.util.List;

public class Step2CargoItemTypeCreationCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(Step2CargoItemTypeCreationCommandHandlerService.class);

    private final CargoItemTypeRepository cargoItemTypeRepository;

    public Step2CargoItemTypeCreationCommandHandlerService(
            CargoDistributorBotService botService,
            CargoItemTypeRepository cargoItemTypeRepository
    ) {
        super(botService);
        this.cargoItemTypeRepository = cargoItemTypeRepository;
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommand(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);
        String cargoItemTypeName = getMessageTextFromUpdate(update);

        if (cargoItemTypeRepository.existsByName(cargoItemTypeName)) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.CARGO_TYPE_NAME_ALREADY_EXISTS.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.ENTER_CARGO_TYPE_NAME.getMessageText()
                    )
            );
            LOGGER.info("Finished processing command, cargo item type with name {} already exists", cargoItemTypeName);
            return resultResponse;
        }

        botService.putCargoItemTypeNameToCache(String.valueOf(chatId), cargoItemTypeName);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.ENTER_CARGO_TYPE_LEGEND.getMessageText()
                )
        );

        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
