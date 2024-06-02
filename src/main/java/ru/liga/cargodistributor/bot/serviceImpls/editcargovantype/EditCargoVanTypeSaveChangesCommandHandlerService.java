package ru.liga.cargodistributor.bot.serviceImpls.editcargovantype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.entity.CargoVanTypeInfo;
import ru.liga.cargodistributor.cargo.repository.CargoVanTypeRepository;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
public class EditCargoVanTypeSaveChangesCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(EditCargoVanTypeSaveChangesCommandHandlerService.class);

    private final CargoVanTypeRepository cargoVanTypeRepository;

    @Autowired
    protected EditCargoVanTypeSaveChangesCommandHandlerService(@Value("${bot.token}") String token, @Value("${cache.capacity}") int cacheCapacity, CargoVanTypeRepository cargoVanTypeRepository) {
        super(token, cacheCapacity);
        this.cargoVanTypeRepository = cargoVanTypeRepository;
    }

    public EditCargoVanTypeSaveChangesCommandHandlerService(
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
    @Transactional
    public List<PartialBotApiMethod<Message>> processCommandAndGetResponseMessages(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);
        CargoVanTypeInfo cargoVanTypeInfoToUpdate = botService.getCargoVanTypeInfoFromCache(String.valueOf(chatId));

        if (cargoVanTypeInfoToUpdate == null) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.EDIT_CARGO_VAN_TYPE_ITEM_NOT_FOUND_IN_CACHE.getMessageText()
                    )
            );

            returnToStart(chatId, resultResponse);
            LOGGER.info("Finished processing command, cargo van type to update not found in cache");
            return resultResponse;
        }

        UUID idToUpdate = cargoVanTypeInfoToUpdate.getId();
        cargoVanTypeRepository.save(cargoVanTypeInfoToUpdate);
        botService.putCargoVanTypeInfoToCache(String.valueOf(chatId), null);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.CARGO_VAN_TYPE_SUCCESSFULLY_UPDATED.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        cargoVanTypeInfoToUpdate.toString()
                )
        );

        returnToStart(chatId, resultResponse);
        LOGGER.info("Finished processing command, cargo van type with id {} successfully updated", idToUpdate);
        return resultResponse;
    }
}
