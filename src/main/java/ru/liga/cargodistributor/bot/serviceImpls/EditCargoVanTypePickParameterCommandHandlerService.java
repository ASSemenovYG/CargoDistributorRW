package ru.liga.cargodistributor.bot.serviceImpls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotKeyboard;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.exceptions.RecordNotFoundException;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.entity.CargoVanTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoVanTypeRepository;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.LinkedList;
import java.util.List;

@Service
public class EditCargoVanTypePickParameterCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(EditCargoVanTypePickParameterCommandHandlerService.class);

    private final CargoVanTypeRepository cargoVanTypeRepository;

    @Autowired
    protected EditCargoVanTypePickParameterCommandHandlerService(@Value("${bot.token}") String token, @Value("${cache.capacity}") int cacheCapacity, CargoVanTypeRepository cargoVanTypeRepository) {
        super(token, cacheCapacity);
        this.cargoVanTypeRepository = cargoVanTypeRepository;
    }

    public EditCargoVanTypePickParameterCommandHandlerService(
            TelegramClient telegramClient,
            CargoDistributorBotService botService,
            CargoConverterService cargoConverterService,
            FileService fileService,
            CargoVanTypeRepository cargoVanTypeRepository
    ) {
        super(telegramClient, botService, cargoConverterService, fileService);
        this.cargoVanTypeRepository = cargoVanTypeRepository;
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommandAndGetResponseMessages(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);
        String cargoVanTypeName = getMessageTextFromUpdate(update);

        CargoVanTypeInfo cargoVanTypeInfoToUpdate;

        try {
            cargoVanTypeInfoToUpdate = cargoVanTypeRepository
                    .findByName(cargoVanTypeName)
                    .orElseThrow(() -> new RecordNotFoundException("Couldn't find cargo van type with name: " + cargoVanTypeName));
        } catch (RecordNotFoundException e) {
            LOGGER.info(e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.CARGO_VAN_TYPE_TO_EDIT_NOT_FOUND.getMessageText() + cargoVanTypeName
                    )
            );

            returnToStart(chatId, resultResponse);

            LOGGER.info("Finished processing command, cargo van type with name {} not found", cargoVanTypeName);
            return resultResponse;
        }

        botService.putCargoVanTypeInfoToCache(String.valueOf(chatId), cargoVanTypeInfoToUpdate);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        cargoVanTypeInfoToUpdate.toString()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.EDIT_CARGO_VAN_TYPE_PICK_PARAMETER.getMessageText(),
                        CargoDistributorBotKeyboard.EDIT_CARGO_VAN_TYPE
                )
        );

        LOGGER.info("Finished processing command");
        return resultResponse;
    }
}
