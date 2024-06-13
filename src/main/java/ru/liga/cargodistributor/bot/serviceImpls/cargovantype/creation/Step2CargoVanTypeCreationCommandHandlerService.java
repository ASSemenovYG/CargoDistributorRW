package ru.liga.cargodistributor.bot.serviceImpls.cargovantype.creation;

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

public class Step2CargoVanTypeCreationCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(Step2CargoVanTypeCreationCommandHandlerService.class);

    private final CargoVanTypeRepository cargoVanTypeRepository;

    public Step2CargoVanTypeCreationCommandHandlerService(
            CargoDistributorBotService botService,
            CargoVanTypeRepository cargoVanTypeRepository
    ) {
        super(botService);
        this.cargoVanTypeRepository = cargoVanTypeRepository;
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommandAndGetResponseMessages(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);
        String cargoVanTypeName = getMessageTextFromUpdate(update);

        if (cargoVanTypeRepository.existsByName(cargoVanTypeName)) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.CARGO_VAN_TYPE_NAME_ALREADY_EXISTS.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_TYPE_NAME.getMessageText()
                    )
            );
            LOGGER.info("Finished processing command, cargo van type with name {} already exists", cargoVanTypeName);
            return resultResponse;
        }

        CargoVanTypeInfo cargoVanTypeInfoToInsert = new CargoVanTypeInfo(cargoVanTypeName);
        botService.putCargoVanTypeInfoToCache(String.valueOf(chatId), cargoVanTypeInfoToInsert);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_TYPE_WIDTH.getMessageText()
                )
        );

        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
