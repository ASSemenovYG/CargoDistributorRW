package ru.liga.cargodistributor.bot.serviceImpls.distibution.fromfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.algorithm.enums.DistributionAlgorithmName;
import ru.liga.cargodistributor.algorithm.serviceImpls.OneVanOneItemDistributionAlgorithmService;
import ru.liga.cargodistributor.algorithm.serviceImpls.SimpleFitDistributionAlgorithmService;
import ru.liga.cargodistributor.algorithm.serviceImpls.SingleSortedCargoDistributionAlgorithmService;
import ru.liga.cargodistributor.algorithm.services.DistributionAlgorithmService;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotKeyboard;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.CargoVanList;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.LinkedList;
import java.util.List;

public class Step4DistributionFromFileCommandHandlerService extends CommandHandlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Step4DistributionFromFileCommandHandlerService.class);

    private final CargoConverterService cargoConverterService;
    private final FileService fileService;

    public Step4DistributionFromFileCommandHandlerService(
            CargoDistributorBotService botService,
            CargoConverterService cargoConverterService,
            FileService fileService
    ) {
        super(botService);
        this.cargoConverterService = cargoConverterService;
        this.fileService = fileService;
    }

    @Override
    public List<PartialBotApiMethod<Message>> processCommand(Update update) {
        LOGGER.info("Started processing command");
        List<PartialBotApiMethod<Message>> resultResponse = new LinkedList<>();
        long chatId = getChatIdFromUpdate(update);

        DistributionAlgorithmService algorithm;
        LOGGER.info("trying to resolve algorithm name from text message: {}", getMessageTextFromUpdate(update));
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

        if (cargoItemList == null || cargoItemList.isEmptyOrNull()) {
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

        try {
            cargoVanList.distributeCargo(algorithm, cargoItemList);
        } catch (RuntimeException e) {
            LOGGER.error("Error occurred during distribution: {}", e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.ERROR_OCCURRED_DURING_DISTRIBUTION.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            "```" + e.getMessage() + "```"
                    )
            );

            returnToStart(chatId, resultResponse);
            return resultResponse;
        }

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
