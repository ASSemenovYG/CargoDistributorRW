package ru.liga.cargodistributor.bot.serviceImpls.cargovantype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.entity.CargoVanTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoVanTypeRepository;

import java.util.LinkedList;
import java.util.List;

public class AllCargoVanTypesGetterCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(AllCargoVanTypesGetterCommandHandlerService.class);

    private final CargoVanTypeRepository cargoVanTypeRepository;

    public AllCargoVanTypesGetterCommandHandlerService(
            CargoDistributorBotService botService,
            CargoVanTypeRepository cargoVanTypeRepository
    ) {
        super(botService);
        this.cargoVanTypeRepository = cargoVanTypeRepository;
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommand(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);

        List<CargoVanTypeInfo> cargoVanTypes = cargoVanTypeRepository.findAll();

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.NUMBER_OF_CARGO_VAN_TYPES_FOUND.getMessageText() +
                                (
                                        (cargoVanTypes == null || cargoVanTypes.isEmpty()) ? 0 : cargoVanTypes.size()
                                )
                )
        );

        if (cargoVanTypes != null && !cargoVanTypes.isEmpty()) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            createMessageTextWithAllFoundCargoVanTypes(cargoVanTypes)
                    )
            );
        }

        returnToStart(chatId, resultResponse);
        LOGGER.info("Finished processing command");
        return resultResponse;
    }

    private String createMessageTextWithAllFoundCargoVanTypes(List<CargoVanTypeInfo> cargoVanTypes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cargoVanTypes.size(); i++) {
            sb.append(cargoVanTypes.get(i).toString());
            if (i < cargoVanTypes.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

}
