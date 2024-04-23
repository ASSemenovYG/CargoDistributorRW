package ru.liga.cargodistributor.bot.commandhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.algorithm.*;
import ru.liga.cargodistributor.bot.CargoDistributorBotKeyboard;
import ru.liga.cargodistributor.bot.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.CargoDistributorBotService;
import ru.liga.cargodistributor.cargo.CargoConverterService;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.CargoVanList;
import ru.liga.cargodistributor.util.FileService;

import java.util.LinkedList;
import java.util.List;

@Service
public class PickAlgorithmCommandHandlerService extends CommandHandlerService {
    //todo: add tests
    private static final Logger LOGGER = LoggerFactory.getLogger(PickAlgorithmCommandHandlerService.class);

    private static final String RESULT_JSON_FILE_NAME = "loadedVans.json";

    @Autowired
    protected PickAlgorithmCommandHandlerService(@Value("${bot.token}") String token, @Value("${cache.capacity}") int cacheCapacity) {
        super(token, cacheCapacity);
    }

    public PickAlgorithmCommandHandlerService(TelegramClient telegramClient, CargoDistributorBotService botService, CargoConverterService cargoConverterService, FileService fileService) {
        super(telegramClient, botService, cargoConverterService, fileService);
    }

    @Override
    public List<Object> processCommandAndGetResponseMessages(Update update) {
        LOGGER.info("Started processing command");
        List<Object> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);

        DistributionAlgorithmService algorithm;
        DistributionAlgorithmName algorithmName = DistributionAlgorithmName.fromString(getMessageTextFromUpdate(update));
        try {
            if (algorithmName == null) {
                throw new RuntimeException("algorithmName cannot be null");
            }

            algorithm = switch (algorithmName) {
                case ONE_VAN_ONE_ITEM -> new OneVanOneItemDistributionAlgorithmService();
                case SINGLE_SORTED -> new SingleSortedCargoDistributionAlgorithmService();
                case SIMPLE_FIT -> new SimpleFitDistributionAlgorithmService();
            };
        } catch (RuntimeException e) {
            LOGGER.error("couldn't resolve algorithm: {}", e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.CANT_RESOLVE_PICKED_ALGORITHM_NAME.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.PICK_ALGORITHM.getMessageText(),
                            CargoDistributorBotKeyboard.PICK_ALGORITHM
                    )
            );
            LOGGER.info("Finished processing command, couldn't resolve algorithm");
            return resultResponse;
        }

        CargoItemList cargoItemList = botService.getCargoItemListFromCache(String.valueOf(chatId));

        if (cargoItemList.isEmptyOrNull()) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.FAILED_TO_FIND_CARGO_LIST.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.SEND_FILE_WITH_CARGO.getMessageText()
                    )
            );

            LOGGER.info("Finished processing command, couldn't find cargo list in cache");
            return resultResponse;
        }

        CargoVanList cargoVanList = new CargoVanList();
        cargoVanList.distributeCargo(algorithm, cargoItemList);

        if (!cargoVanList.isListSizeLessOrEqualThanMaxSize(botService.getVanLimitFromCache(String.valueOf(chatId)))) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.FAILED_TO_DISTRIBUTE_UNDER_VAN_LIMIT.getMessageText()
                    )
            );

            returnToStart(chatId, resultResponse);
            LOGGER.info("Finished processing command, distribution doesn't fit van limit");
            return resultResponse;
        }

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.DISTRIBUTION_RESULT.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        "```" + cargoVanList.getCargoVanListAsString(cargoConverterService) + "```"
                )
        );

        String jsonFileName;
        try {
            jsonFileName = fileService.writeStringToFile(cargoConverterService.serializeLoadedVansToJson(cargoVanList));
        } catch (RuntimeException e) {
            LOGGER.error("error while creating JSON file: {}", e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.ERROR_WHILE_CREATING_DISTRIBUTION_RESULT_FILE.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            "```" + e.getMessage() + "```"
                    )
            );

            returnToStart(chatId, resultResponse);
            LOGGER.info("Finished processing command, error occurred while creating JSON file with distribution result");
            return resultResponse;
        }

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.DISTRIBUTION_RESULT_IN_A_FILE.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildDocumentMessage(
                        chatId,
                        jsonFileName,
                        RESULT_JSON_FILE_NAME
                )
        );

        returnToStart(chatId, resultResponse);
        LOGGER.info("Finished processing command, successful distribution");
        return resultResponse;
    }
}
