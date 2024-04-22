package ru.liga.cargodistributor.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.algorithm.*;
import ru.liga.cargodistributor.cargo.CargoConverterService;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.CargoVanList;
import ru.liga.cargodistributor.util.FileService;

import java.util.LinkedList;
import java.util.List;

@Component
public class CargoDistributorBotUpdateHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CargoDistributorBotUpdateHandler.class);

    private static final String RESULT_JSON_FILE_NAME = "loadedVans.json";
    private static final String SRC_RESOURCES_PATH = "src/main/resources/";
    private static final String CARGO_FILE_EXAMPLE_NAME = "cargo_file_example.pkg";
    private static final String LOADED_VANS_FILE_EXAMPLE_NAME = "loadedVansExample.json";
    private static final String CARGO_FILE_EXAMPLE_PATH = SRC_RESOURCES_PATH + CARGO_FILE_EXAMPLE_NAME;
    private static final String LOADED_VANS_FILE_EXAMPLE_PATH = SRC_RESOURCES_PATH + LOADED_VANS_FILE_EXAMPLE_NAME;
    private static final String HELP_COMMAND_STICKER_ID = "CAACAgIAAxkBAAEL9bxmJFPTU9efBMHORW2P0MoLt4KSWQACWwIAAvNWPxdFcg4Bd_Sh0zQE";
    private static final String ABOUT_COMMAND_VIDEO_ID = "BAACAgIAAxkBAAPnZiRfZOTOCEhcoD205Iz6fDSiTbMAAqZTAALkTSBJd9KpquJL27A0BA";
    private static final String ABOUT_COMMAND_STICKER_ID_1 = "CAACAgIAAxkBAAEL9b5mJFsmbk4g6TvaIYybFKi2wDtuNQACSSwAAnodcUrY0E6TPhUOwjQE";
    private static final String ABOUT_COMMAND_STICKER_ID_2 = "CAACAgIAAxkBAAEL9cBmJFsw7zOK8bp2_y8wmFE1DM-3WAACMC8AAnXKaEp_pe78p-vsCzQE";
    private static final String ABOUT_COMMAND_STICKER_ID_3 = "CAACAgIAAxkBAAEL9cJmJFs8iNpo14WkcPqPNVfFtK3-CQACqi0AAmTJaUqOKPZ9kj4LQjQE";

    private final TelegramClient telegramClient;
    private final CargoDistributorBotService botService;
    private final CargoConverterService cargoConverterService;
    private final FileService fileService;

    @Autowired
    public CargoDistributorBotUpdateHandler(TelegramClient telegramClient, CargoDistributorBotService botService, CargoConverterService cargoConverterService, FileService fileService) {
        this.telegramClient = telegramClient;
        this.botService = botService;
        this.cargoConverterService = cargoConverterService;
        this.fileService = fileService;
    }

    public List<Object> processUpdateAndGetResponseMessages(Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        List<Object> resultResponse = new LinkedList<>();

        if (updateHasMessageText(update) && messageText.equals(CargoDistributorBotUserCommand.START.getCommandText())) {
            processStartCommand(chatId, resultResponse);
        } else if (
                updateHasMessageText(update) &&
                        (
                                messageText.equals(CargoDistributorBotResponseMessage.DISTRIBUTE_CARGO.getMessageText()) ||
                                        messageText.equals(CargoDistributorBotUserCommand.DISTRIBUTE.getCommandText())
                        )
        ) {
            processDistributeCommand(chatId, resultResponse);
        } else if (
                updateHasMessageText(update) &&
                        (
                                messageText.equals(CargoDistributorBotResponseMessage.READ_JSON_WITH_LOADED_VANS.getMessageText()) ||
                                        messageText.equals(CargoDistributorBotUserCommand.READ_CARGO.getCommandText())
                        )
        ) {
            processReadCargoCommand(chatId, resultResponse);
        } else if (
                !updateHasMessageText(update) &&
                        update.getMessage().hasDocument() &&
                        isLastSendMessageEqualTo(chatId, CargoDistributorBotResponseMessage.SEND_FILE_WITH_CARGO.getMessageText())
        ) {
            processCargoListCommand(chatId, update, resultResponse);
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(chatId, CargoDistributorBotResponseMessage.ENTER_VAN_LIMIT.getMessageText())
        ) {
            processReadVanLimitCommand(chatId, messageText, resultResponse);
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(chatId, CargoDistributorBotResponseMessage.PICK_ALGORITHM.getMessageText())
        ) {
            processPickAlgorithmCommand(chatId, messageText, resultResponse);
        } else if (
                isLastSendMessageEqualTo(chatId, CargoDistributorBotResponseMessage.SEND_LOADED_VANS_TO_READ.getMessageText()) &&
                        (
                                updateHasMessageText(update) ||
                                        update.getMessage().hasDocument()
                        )
        ) {
            processReadCargoVansCommand(chatId, update, resultResponse);
        } else if (
                updateHasMessageText(update) &&
                        messageText.equals(CargoDistributorBotUserCommand.HELP.getCommandText())
        ) {
            processHelpCommand(chatId, resultResponse);
        } else if (
                updateHasMessageText(update) &&
                        messageText.equals(CargoDistributorBotUserCommand.ABOUT.getCommandText())
        ) {
            processAboutCommand(chatId, resultResponse);
        } else {
            processUnknownCommand(chatId, resultResponse);
        }

        return resultResponse;
    }

    //todo: в идеале это все эти методы надо размазать по абстрактным классам или интерфейсу
    private void processStartCommand(long chatId, List<Object> resultResponse) {
        resultResponse.add(
                botService.buildTextMessageWithKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.LOOK_WHAT_I_CAN_DO.getMessageText(),
                        CargoDistributorBotKeyboard.START
                )
        );
    }

    private void processDistributeCommand(long chatId, List<Object> resultResponse) {
        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.SEND_FILE_WITH_CARGO.getMessageText()
                )
        );
    }

    private void processReadCargoCommand(long chatId, List<Object> resultResponse) {
        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.SEND_LOADED_VANS_TO_READ.getMessageText()
                )
        );
    }

    private void processCargoListCommand(long chatId, Update update, List<Object> resultResponse) {
        CargoItemList cargoList;
        try {
            cargoList = new CargoItemList(
                    cargoConverterService.parseCargoItems(
                            fileService.readFromFile(
                                    botService.getFileFromUpdate(update, telegramClient)
                            )
                    )
            );
        } catch (RuntimeException e) {
            LOGGER.error("processCargoListCommand: {}", e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.ERROR_WHILE_PROCESSING_CARGO_FILE.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            "```" + e.getMessage() + "```"
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
                            CargoDistributorBotResponseMessage.SEND_FILE_WITH_CARGO.getMessageText()
                    )
            );
            return;
        }

        if (cargoList.isEmptyOrNull()) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.NO_CARGO_ITEMS_FOUND_IN_A_FILE.getMessageText()
                    )
            );

            returnToStart(chatId, resultResponse);
            return;
        }

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.FOUND_CARGO_ITEMS_IN_A_FILE.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        "```" + cargoList.getCargoItemNamesAsString() + "```"
                )
        );

        botService.putCargoItemListToCache(String.valueOf(chatId), cargoList);

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_LIMIT.getMessageText()
                )
        );
    }

    private void processReadVanLimitCommand(long chat_id, String messageText, List<Object> resultResponse) {
        int vanLimit;
        try {
            vanLimit = Integer.parseInt(messageText);
        } catch (NumberFormatException e) {
            LOGGER.error("processReadVanLimitCommand: {}", e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chat_id,
                            CargoDistributorBotResponseMessage.FAILED_TO_PARSE_INTEGER.getMessageText()
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chat_id,
                            CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_LIMIT.getMessageText()
                    )
            );

            return;
        }
        botService.putVanLimitToCache(String.valueOf(chat_id), vanLimit);

        resultResponse.add(
                botService.buildTextMessageWithKeyboard(
                        chat_id,
                        CargoDistributorBotResponseMessage.PICK_ALGORITHM.getMessageText(),
                        CargoDistributorBotKeyboard.PICK_ALGORITHM
                )
        );
    }

    private void processPickAlgorithmCommand(long chatId, String messageText, List<Object> resultResponse) {
        DistributionAlgorithmService algorithm;
        DistributionAlgorithmName algorithmName = DistributionAlgorithmName.fromString(messageText);
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
            LOGGER.error("processPickAlgorithmCommand: couldn't resolve algorithm: {}", e.getMessage());

            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            "Я не понял, какой алгоритм ты выбрал"
                    )
            );

            resultResponse.add(
                    botService.buildTextMessageWithKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.PICK_ALGORITHM.getMessageText(),
                            CargoDistributorBotKeyboard.PICK_ALGORITHM
                    )
            );
            return;
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

            return;
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
            return;
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
            LOGGER.error("processPickAlgorithmCommand: error while creating JSON file: {}", e.getMessage());

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
            return;
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
    }

    private void processReadCargoVansCommand(long chatId, Update update, List<Object> resultResponse) {
        CargoVanList cargoVanList;
        if (update.getMessage().hasDocument()) {
            try {
                cargoVanList = cargoConverterService.deserializeLoadedVansFromJson(
                        fileService.readFromFile(
                                botService.getFileFromUpdate(update, telegramClient)
                        )
                );
            } catch (RuntimeException e) {
                LOGGER.error("processReadCargoVansCommand: error while deserializing file: {}", e.getMessage());

                resultResponse.add(
                        botService.buildTextMessageWithoutKeyboard(
                                chatId,
                                CargoDistributorBotResponseMessage.ERROR_WHILE_PROCESSING_CARGO_VAN_FILE.getMessageText()
                        )
                );

                resultResponse.add(
                        botService.buildTextMessageWithoutKeyboard(
                                chatId,
                                "```" + e.getMessage() + "```"
                        )
                );

                returnToStart(chatId, resultResponse);
                return;
            }
        } else {
            try {
                cargoVanList = cargoConverterService.deserializeLoadedVansFromJson(update.getMessage().getText());
            } catch (RuntimeException e) {
                LOGGER.error("processReadCargoVansCommand: error while deserializing JSON from message: {}", e.getMessage());

                resultResponse.add(
                        botService.buildTextMessageWithoutKeyboard(
                                chatId,
                                CargoDistributorBotResponseMessage.ERROR_WHILE_PROCESSING_CARGO_VAN_JSON_MESSAGE.getMessageText()
                        )
                );

                resultResponse.add(
                        botService.buildTextMessageWithoutKeyboard(
                                chatId,
                                "```" + e.getMessage() + "```"
                        )
                );

                returnToStart(chatId, resultResponse);
                return;
            }
        }

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.NUMBER_OF_READ_VANS.getMessageText() + cargoVanList.getCargoVans().size()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.DISTRIBUTION_OF_CARGO_FROM_VANS.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        "```" + cargoVanList.getCargoVanListAsString(cargoConverterService) + "```"
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.CARGO_LIST_FROM_VANS.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        "```" + cargoVanList.getAllCargoItemNamesAsString() + "```"
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.NUMBER_OF_ITEMS_FROM_VANS.getMessageText() + cargoVanList.getAllCargoItemsFromVans().size())
        );

        returnToStart(chatId, resultResponse);
    }

    private void returnToStart(long chatId, List<Object> resultResponse) {
        resultResponse.add(
                botService.buildTextMessageWithKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.RETURNING_TO_START.getMessageText(),
                        CargoDistributorBotKeyboard.START
                )
        );
    }

    private void processHelpCommand(long chatId, List<Object> resultResponse) {
        resultResponse.add(
                botService.buildStickerMessage(
                        chatId,
                        HELP_COMMAND_STICKER_ID
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.HELP_DISTRIBUTE_COMMAND_DESCRIPTION.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildDocumentMessage(
                        chatId,
                        CARGO_FILE_EXAMPLE_PATH,
                        CARGO_FILE_EXAMPLE_NAME
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.HELP_DISTRIBUTE_COMMAND_RUN.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.HELP_READCARGO_COMMAND_DESCRIPTION.getMessageText()
                )
        );

        resultResponse.add(
                botService.buildDocumentMessage(
                        chatId,
                        LOADED_VANS_FILE_EXAMPLE_PATH,
                        LOADED_VANS_FILE_EXAMPLE_NAME
                )
        );

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.HELP_READCARGO_COMMAND_RUN.getMessageText()
                )
        );

        returnToStart(chatId, resultResponse);
    }

    private void processAboutCommand(long chatId, List<Object> resultResponse) {
        resultResponse.add(
                botService.buildMessageWithVideo(
                        chatId,
                        ABOUT_COMMAND_VIDEO_ID
                )
        );

        resultResponse.add(
                botService.buildStickerMessage(
                        chatId,
                        ABOUT_COMMAND_STICKER_ID_1
                )
        );

        resultResponse.add(
                botService.buildStickerMessage(
                        chatId,
                        ABOUT_COMMAND_STICKER_ID_2
                )
        );

        resultResponse.add(
                botService.buildStickerMessage(
                        chatId, ABOUT_COMMAND_STICKER_ID_3
                )
        );

        returnToStart(chatId, resultResponse);
    }

    private void processUnknownCommand(long chatId, List<Object> resultResponse) {
        SendMessage lastMessage = botService.getLastSendMessageFromCache(String.valueOf(chatId));

        if (lastMessage == null) {
            resultResponse.add(
                    botService.buildTextMessageWithoutKeyboard(
                            chatId,
                            CargoDistributorBotResponseMessage.CANT_PROCESS_LAST_MESSAGE.getMessageText()
                    )
            );

            returnToStart(chatId, resultResponse);
            return;
        }

        resultResponse.add(
                botService.buildTextMessageWithoutKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.CANT_PROCESS_LAST_MESSAGE_FOUND_PREVIOUS_RESPONSE.getMessageText()
                )
        );

        resultResponse.add(lastMessage);
    }

    private boolean updateHasMessageText(Update update) {
        if (update.getMessage().hasText()) {
            return update.getMessage().getText() != null &&
                    !update.getMessage().getText().isBlank() &&
                    !update.getMessage().getText().isEmpty();
        }
        return false;
    }

    private boolean isLastSendMessageEqualTo(long chatId, String messageText) {
        SendMessage message = botService.getLastSendMessageFromCache(String.valueOf(chatId));
        if (message == null) {
            return false;
        }
        return message
                .getText()
                .equals(messageText);
    }
}
