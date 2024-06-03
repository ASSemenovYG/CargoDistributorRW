package ru.liga.cargodistributor.bot.serviceImpls.addcargovantype;

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

@Service
public class AddCargoVanTypeEnterLengthCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(AddCargoVanTypeEnterLengthCommandHandlerService.class);

    private final CargoVanTypeRepository cargoVanTypeRepository;

    @Autowired
    protected AddCargoVanTypeEnterLengthCommandHandlerService(@Value("${bot.token}") String token, @Value("${cache.capacity}") int cacheCapacity, CargoVanTypeRepository cargoVanTypeRepository) {
        super(token, cacheCapacity);
        this.cargoVanTypeRepository = cargoVanTypeRepository;
    }

    public AddCargoVanTypeEnterLengthCommandHandlerService(
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

        int vanLength;
        try {
            vanLength = Integer.parseInt(getMessageTextFromUpdate(update));
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.FAILED_TO_PARSE_INTEGER.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.TRY_AGAIN.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_TYPE_LENGTH.getMessageText()
                    )
            );

            LOGGER.info("Finished processing command, error occurred while parsing Integer");
            return resultResponse;
        }

        if (vanLength < 1) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.NEED_TO_ENTER_INTEGER_GREATER_THAN_ZERO.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.TRY_AGAIN.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_TYPE_LENGTH.getMessageText()
                    )
            );

            LOGGER.info("Finished processing command, user entered invalid length: {}", vanLength);
            return resultResponse;
        }

        CargoVanTypeInfo cargoVanTypeInfoToInsert = botService.getCargoVanTypeInfoFromCache(String.valueOf(chatId));

        if (cargoVanTypeInfoToInsert == null) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.FAILED_TO_FIND_CARGO_VAN_TYPE_TO_INSERT.getMessageText()
                    )
            );

            returnToStart(chatId, resultResponse);
            LOGGER.info("Finished processing command, cargo van type to insert not found in cache");
            return resultResponse;
        }

        cargoVanTypeInfoToInsert.setLength(vanLength);

        cargoVanTypeRepository.save(cargoVanTypeInfoToInsert);
        String cargoVanTypeName = cargoVanTypeInfoToInsert.getName();
        botService.putCargoVanTypeInfoToCache(String.valueOf(chatId), null);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.CARGO_VAN_TYPE_SUCCESSFULLY_ADDED.getMessageText() + cargoVanTypeName
                )
        );

        returnToStart(chatId, resultResponse);

        LOGGER.info("Finished processing command, cargo van type with ID {} successfully added", cargoVanTypeInfoToInsert.getId());
        return resultResponse;
    }
}
