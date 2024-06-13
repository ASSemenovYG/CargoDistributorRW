package ru.liga.cargodistributor.bot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotKeyboard;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotKeyboardButton;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotUserCommand;
import ru.liga.cargodistributor.bot.serviceImpls.cargoitemtype.AllCargoItemTypesGetterCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.cargoitemtype.change.*;
import ru.liga.cargodistributor.bot.serviceImpls.cargoitemtype.creation.*;
import ru.liga.cargodistributor.bot.serviceImpls.cargoitemtype.deletion.Step1CargoItemTypeDeletionCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.cargoitemtype.deletion.Step2CargoItemTypeDeletionCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.cargoload.reader.Step1CargoLoadReaderCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.cargoload.reader.Step2CargoLoadReaderCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.cargoload.reader.Step2CargoLoadReaderFileErrorCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.cargovantype.AllCargoVanTypesGetterCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.cargovantype.change.*;
import ru.liga.cargodistributor.bot.serviceImpls.cargovantype.creation.Step1CargoVanTypeCreationCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.cargovantype.creation.Step2CargoVanTypeCreationCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.cargovantype.creation.Step3CargoVanTypeCreationCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.cargovantype.creation.Step4CargoVanTypeCreationCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.cargovantype.deletion.Step1CargoVanTypeDeletionCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.cargovantype.deletion.Step2CargoVanTypeDeletionCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.common.*;
import ru.liga.cargodistributor.bot.serviceImpls.distibution.bytypes.*;
import ru.liga.cargodistributor.bot.serviceImpls.distibution.fromfile.*;
import ru.liga.cargodistributor.cargo.repository.CargoItemTypeRepository;
import ru.liga.cargodistributor.cargo.repository.CargoVanTypeRepository;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.List;

public abstract class CommandHandlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandlerService.class);
    public static final String RESULT_JSON_FILE_NAME = "loadedVans.json";

    protected final CargoDistributorBotService botService;

    public CommandHandlerService(CargoDistributorBotService botService) {
        this.botService = botService;
    }

    public abstract List<PartialBotApiMethod<Message>> processCommand(Update update);

    public static CommandHandlerService determineCommandHandler(
            Update update,
            CargoDistributorBotService botService,
            SendMessage lastSendMessage,
            TelegramClient telegramClient,
            CargoConverterService cargoConverterService,
            FileService fileService,
            CargoItemTypeRepository cargoItemTypeRepository,
            CargoVanTypeRepository cargoVanTypeRepository
    ) {
        //todo: в бОльшей части хендлеров можно зашивать следующий шаг в кеш в конце обработки, тогда почти всю портянку if-else можно будет убрать
        CommandHandlerService handlerService;
        if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotUserCommand.START.getCommandText())
        ) {
            handlerService = new StartCommandHandlerService(botService);
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.DISTRIBUTE_BY_SELECTED_TYPES.getButtonText())
        ) {
            //todo: add tests for this scenario
            handlerService = new Step1DistributionByTypesCommandHandlerService(
                    botService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ENTER_CARGO_VAN_TYPE_NAME.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step2DistributionByTypesCommandHandlerService(
                    botService,
                    cargoVanTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ENTER_CARGO_ITEM_TYPE_NAME.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step3_0DistributionByTypesCommandHandlerService(
                    botService,
                    cargoItemTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_CARGO_ITEM_TYPE_WITH_SUCH_LEGEND_ALREADY_ADDED.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step3_1DistributionByTypesCommandHandlerService(
                    botService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ENTER_CARGO_ITEM_TYPE_COUNT.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step3_3DistributionByTypesCommandHandlerService(
                    botService
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.DISTRIBUTE_BY_TYPES_ADD_ONE_MORE_CARGO_TYPE.getButtonText()) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ADD_MORE_CARGO_TYPE_OR_CONTINUE.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step3_4DistributionByTypesCommandHandlerService(
                    botService
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.DISTRIBUTE_BY_TYPES_SELECT_ALGORITHM.getButtonText()) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ADD_MORE_CARGO_TYPE_OR_CONTINUE.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step4DistributionByTypesCommandHandlerService(
                    botService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_PICK_ALGORITHM.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step5DistributionByTypesCommandHandlerService(
                    botService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ENTER_VAN_LIMIT.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step6DistributionByTypesCommandHandlerService(
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.ADD_CARGO_VAN_TYPE.getButtonText())
        ) {
            //todo: add tests for this scenario
            handlerService = new Step1CargoVanTypeCreationCommandHandlerService(
                    botService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_TYPE_NAME.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step2CargoVanTypeCreationCommandHandlerService(
                    botService,
                    cargoVanTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_TYPE_WIDTH.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step3CargoVanTypeCreationCommandHandlerService(
                    botService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_TYPE_LENGTH.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step4CargoVanTypeCreationCommandHandlerService(
                    botService,
                    cargoVanTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.EDIT_CARGO_VAN_TYPE.getButtonText())
        ) {
            //todo: add tests for this scenario
            handlerService = new Step1_CargoVanTypeChangeCommandHandlerService(
                    botService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.EDIT_CARGO_VAN_ENTER_CARGO_VAN_TYPE_NAME.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step2_CargoVanTypeChangeCommandHandlerService(
                    botService,
                    cargoVanTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.EDIT_CARGO_VAN_TYPE_NAME.getButtonText()) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.EDIT_CARGO_VAN_TYPE_PICK_PARAMETER.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step3_Name_1_CargoVanTypeChangeCommandHandlerService(
                    botService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_NEW_CARGO_VAN_TYPE_NAME.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step3_Name_2_CargoVanTypeChangeCommandHandlerService(
                    botService,
                    cargoVanTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.EDIT_CARGO_VAN_TYPE_WIDTH.getButtonText()) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.EDIT_CARGO_VAN_TYPE_PICK_PARAMETER.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step3_Width_1_CargoVanTypeChangeCommandHandlerService(
                    botService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_NEW_CARGO_VAN_TYPE_WIDTH.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step3_Width_2_CargoVanTypeChangeCommandHandlerService(
                    botService
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.EDIT_CARGO_VAN_TYPE_LENGTH.getButtonText()) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.EDIT_CARGO_VAN_TYPE_PICK_PARAMETER.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step3_Length_1_CargoVanTypeChangeCommandHandlerService(
                    botService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_NEW_CARGO_VAN_TYPE_LENGTH.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step3_Length_2_CargoVanTypeChangeCommandHandlerService(
                    botService
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.EDIT_CARGO_VAN_TYPE_SAVE_CHANGES.getButtonText())
        ) {
            //todo: add tests for this scenario
            handlerService = new Step4_CargoVanTypeChangeCommandHandlerService(
                    botService,
                    cargoVanTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.DELETE_CARGO_VAN_TYPE.getButtonText())
        ) {
            //todo: add tests for this scenario
            handlerService = new Step1CargoVanTypeDeletionCommandHandlerService(
                    botService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_TYPE_NAME_TO_DELETE.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step2CargoVanTypeDeletionCommandHandlerService(
                    botService,
                    cargoVanTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.GET_ALL_CARGO_VAN_TYPES.getButtonText())
        ) {
            //todo: add tests for this scenario
            handlerService = new AllCargoVanTypesGetterCommandHandlerService(
                    botService,
                    cargoVanTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.GET_ALL_CARGO_TYPES.getButtonText())
        ) {
            //todo: add tests for this scenario
            handlerService = new AllCargoItemTypesGetterCommandHandlerService(
                    botService,
                    cargoItemTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.ADD_CARGO_TYPE.getButtonText())
        ) {
            //todo: add tests for this scenario
            handlerService = new Step1CargoItemTypeCreationCommandHandlerService(botService);
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_CARGO_TYPE_NAME.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step2CargoItemTypeCreationCommandHandlerService(
                    botService,
                    cargoItemTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_CARGO_TYPE_LEGEND.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step3CargoItemTypeCreationCommandHandlerService(
                    botService
            );
        } else if (
                !updateHasMessageText(update) &&
                        update.getMessage().hasDocument() &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.SEND_FILE_WITH_SINGLE_CARGO.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = determineCommandHandlerAfterParsingUpdateDocument(
                    "Step4CargoItemTypeCreation",
                    update,
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    cargoItemTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.EDIT_CARGO_TYPE.getButtonText())
        ) {
            //todo: add tests for this scenario
            handlerService = new Step1_CargoItemTypeChangeCommandHandlerService(botService);
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.EDIT_CARGO_ENTER_CARGO_TYPE_NAME.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step2_CargoItemTypeChangeCommandHandlerService(
                    botService,
                    cargoItemTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.EDIT_CARGO_TYPE_NAME.getButtonText()) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.EDIT_CARGO_TYPE_PICK_PARAMETER.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step3_Name_1_CargoItemTypeChangeCommandHandlerService(
                    botService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_NEW_CARGO_TYPE_NAME.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step3_Name_2_CargoItemTypeChangeCommandHandlerService(
                    botService,
                    cargoItemTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.EDIT_CARGO_TYPE_LEGEND.getButtonText()) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.EDIT_CARGO_TYPE_PICK_PARAMETER.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step3_Legend_1_CargoItemTypeChangeCommandHandlerService(
                    botService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_NEW_CARGO_TYPE_LEGEND.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step3_Legend_2_CargoItemTypeChangeCommandHandlerService(
                    botService
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.EDIT_CARGO_TYPE_SHAPE.getButtonText()) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.EDIT_CARGO_TYPE_PICK_PARAMETER.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new Step3_Shape_1_CargoItemTypeChangeCommandHandlerService(
                    botService
            );
        } else if (
                !updateHasMessageText(update) &&
                        update.getMessage().hasDocument() &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.SEND_FILE_WITH_SINGLE_CARGO_NEW_SHAPE.getMessageText(), lastSendMessage)

        ) {
            //todo: add tests for this scenario
            handlerService = determineCommandHandlerAfterParsingUpdateDocument(
                    "Step3_Shape_2_CargoItemTypeChange",
                    update,
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    cargoItemTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.EDIT_CARGO_TYPE_SAVE_CHANGES.getButtonText()) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.EDIT_CARGO_TYPE_PICK_PARAMETER.getMessageText(), lastSendMessage)

        ) {
            //todo: add tests for this scenario
            handlerService = new Step4_CargoItemTypeChangeCommandHandlerService(
                    botService,
                    cargoItemTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.DELETE_CARGO_TYPE.getButtonText())
        ) {
            handlerService = new Step1CargoItemTypeDeletionCommandHandlerService(botService);
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_CARGO_TYPE_NAME_TO_DELETE.getMessageText(), lastSendMessage)
        ) {
            handlerService = new Step2CargoItemTypeDeletionCommandHandlerService(
                    botService,
                    cargoItemTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        (
                                isUpdateMessageTextEqualTo(update, CargoDistributorBotResponseMessage.DISTRIBUTE_CARGO.getMessageText()) ||
                                        isUpdateMessageTextEqualTo(update, CargoDistributorBotUserCommand.DISTRIBUTE.getCommandText())
                        )
        ) {
            handlerService = new Step1DistributionFromFileCommandHandlerService(botService);
        } else if (
                updateHasMessageText(update) &&
                        (
                                isUpdateMessageTextEqualTo(update, CargoDistributorBotResponseMessage.READ_JSON_WITH_LOADED_VANS.getMessageText()) ||
                                        isUpdateMessageTextEqualTo(update, CargoDistributorBotUserCommand.READ_CARGO.getCommandText())
                        )
        ) {
            handlerService = new Step1CargoLoadReaderCommandHandlerService(botService);
        } else if (
                !updateHasMessageText(update) &&
                        update.getMessage().hasDocument() &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.SEND_FILE_WITH_CARGO.getMessageText(), lastSendMessage)
        ) {
            handlerService = determineCommandHandlerAfterParsingUpdateDocument(
                    "Step2DistributionFromFile",
                    update,
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    cargoItemTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_VAN_LIMIT.getMessageText(), lastSendMessage)
        ) {
            handlerService = new Step3DistributionFromFileCommandHandlerService(botService);
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.PICK_ALGORITHM.getMessageText(), lastSendMessage)
        ) {
            handlerService = new Step4DistributionFromFileCommandHandlerService(botService, cargoConverterService, fileService);
        } else if (
                isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.SEND_LOADED_VANS_TO_READ.getMessageText(), lastSendMessage) &&
                        (
                                updateHasMessageText(update) ||
                                        update.getMessage().hasDocument()
                        )
        ) {
            if (update.getMessage().hasDocument()) {
                handlerService = determineCommandHandlerAfterParsingUpdateDocument(
                        "Step2CargoReading",
                        update,
                        telegramClient,
                        botService,
                        cargoConverterService,
                        fileService,
                        cargoItemTypeRepository
                );
            } else {
                handlerService = new Step2CargoLoadReaderCommandHandlerService(
                        botService,
                        cargoConverterService,
                        null
                );
            }

        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotUserCommand.HELP.getCommandText())
        ) {
            handlerService = new HelpCommandHandlerService(botService);
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotUserCommand.ABOUT.getCommandText())
        ) {
            handlerService = new AboutCommandHandlerService(botService);
        } else {
            handlerService = new UnknownCommandHandlerService(botService);
        }
        LOGGER.info("Picked command handler: {}", handlerService.getClass().getName());
        return handlerService;
    }

    protected void returnToStart(long chatId, List<PartialBotApiMethod<Message>> resultResponse) {
        resultResponse.add(
                botService.buildTextMessageWithKeyboard(
                        chatId,
                        CargoDistributorBotResponseMessage.RETURNING_TO_START.getMessageText(),
                        CargoDistributorBotKeyboard.START
                )
        );
    }

    protected long getChatIdFromUpdate(Update update) {
        return update.getMessage().getChatId();
    }

    protected String getMessageTextFromUpdate(Update update) {
        return update.getMessage().getText();
    }

    private static boolean updateHasMessageText(Update update) {
        if (update.getMessage().hasText()) {
            return update.getMessage().getText() != null &&
                    !update.getMessage().getText().isBlank() &&
                    !update.getMessage().getText().isEmpty();
        }
        return false;
    }

    private static boolean isUpdateMessageTextEqualTo(Update update, String stringToCompare) {
        String messageText = update.getMessage().getText();
        return messageText.equals(stringToCompare);
    }

    private static boolean isLastSendMessageEqualTo(String messageText, SendMessage lastSendMessage) {
        if (lastSendMessage == null) {
            return false;
        }
        return lastSendMessage
                .getText()
                .equals(messageText);
    }

    private static CommandHandlerService determineCommandHandlerAfterParsingUpdateDocument(
            String handlerName,
            Update update,
            TelegramClient telegramClient,
            CargoDistributorBotService botService,
            CargoConverterService cargoConverterService,
            FileService fileService,
            CargoItemTypeRepository cargoItemTypeRepository
    ) {
        String docContent = null;
        String errorMessage = null;
        CommandHandlerService handlerService;
        try {
            docContent = fileService.readFromFile(botService.getFileFromUpdate(update, telegramClient));
        } catch (RuntimeException e) {
            errorMessage = String.format("error while reading content from file: %s", e.getMessage());
            LOGGER.error(errorMessage);
        }

        switch (handlerName) {
            case "Step2DistributionFromFile": {
                if (errorMessage == null) {
                    handlerService = new Step2DistributionFromFileCommandHandlerService(
                            botService,
                            cargoConverterService,
                            docContent
                    );
                } else {
                    handlerService = new Step2DistributionFromFileReaderErrorCommandHandlerService(
                            botService,
                            errorMessage
                    );
                }

            }
            break;
            case "Step2CargoReading": {
                if (errorMessage == null) {
                    handlerService = new Step2CargoLoadReaderCommandHandlerService(
                            botService,
                            cargoConverterService,
                            docContent
                    );
                } else {
                    handlerService = new Step2CargoLoadReaderFileErrorCommandHandlerService(
                            botService,
                            errorMessage
                    );
                }
            }
            break;
            case "Step4CargoItemTypeCreation": {
                if (errorMessage == null) {
                    handlerService = new Step4CargoItemTypeCreationCommandHandlerService(
                            botService,
                            cargoConverterService,
                            cargoItemTypeRepository,
                            docContent
                    );
                } else {
                    handlerService = new Step4CargoItemTypeCreationFileErrorCommandHandlerService(
                            botService,
                            errorMessage
                    );
                }
            }
            break;
            case "Step3_Shape_2_CargoItemTypeChange": {
                if (errorMessage == null) {
                    handlerService = new Step3_Shape_2_CargoItemTypeChangeCommandHandlerService(
                            botService,
                            cargoConverterService,
                            docContent
                    );
                } else {
                    handlerService = new Step3_Shape_2_CargoItemTypeChangeFileErrorCommandHandlerService(
                            botService,
                            errorMessage
                    );
                }
            }
            break;
            default: {
                LOGGER.info("determineCommandHandlerAfterParsingUpdateDocument: Unknown handler name: {}", handlerName);
                errorMessage += "\nUnknown handler name: " + handlerName;

                handlerService = new FileReaderErrorUnknownCommandHandlerService(
                        botService,
                        errorMessage
                );
            }
        }
        return handlerService;
    }
}
