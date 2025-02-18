package ru.liga.cargodistributor.bot.serviceImpls.cargoitemtype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoItemTypeRepository;

import java.util.LinkedList;
import java.util.List;

public class AllCargoItemTypesGetterCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(AllCargoItemTypesGetterCommandHandlerService.class);

    private final CargoItemTypeRepository cargoItemTypeRepository;

    public AllCargoItemTypesGetterCommandHandlerService(
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

        List<CargoItemTypeInfo> cargoItemTypes = cargoItemTypeRepository.findAll();

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.NUMBER_OF_CARGO_TYPES_FOUND.getMessageText() +
                                (
                                        (cargoItemTypes == null || cargoItemTypes.isEmpty()) ? 0 : cargoItemTypes.size()
                                )
                )
        );

        if (cargoItemTypes != null && !cargoItemTypes.isEmpty()) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            createMessageTextWithAllFoundCargoItemTypes(cargoItemTypes)
                    )
            );
        }

        returnToStart(chatId, resultResponse);
        LOGGER.info("Finished processing command");
        return resultResponse;
    }

    private String createMessageTextWithAllFoundCargoItemTypes(List<CargoItemTypeInfo> cargoItemTypes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cargoItemTypes.size(); i++) {
            sb.append(cargoItemTypes.get(i).toString());
            if (i < cargoItemTypes.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

}
