package ru.liga.cargodistributor.bot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotKeyboard;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotKeyboardButton;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotUserCommand;
import ru.liga.cargodistributor.bot.serviceImpls.addcargotype.*;
import ru.liga.cargodistributor.bot.serviceImpls.addcargovantype.AddCargoVanTypeCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.addcargovantype.AddCargoVanTypeEnterLengthCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.addcargovantype.AddCargoVanTypeEnterNameCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.addcargovantype.AddCargoVanTypeEnterWidthCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.common.*;
import ru.liga.cargodistributor.bot.serviceImpls.deletecargotype.DeleteCargoTypeCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.deletecargotype.DeleteCargoTypeEnterNameCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.deletecargovantype.DeleteCargoVanTypeCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.deletecargovantype.DeleteCargoVanTypeEnterNameCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.distributebytypes.*;
import ru.liga.cargodistributor.bot.serviceImpls.distributefromfile.*;
import ru.liga.cargodistributor.bot.serviceImpls.editcargotype.*;
import ru.liga.cargodistributor.bot.serviceImpls.editcargovantype.*;
import ru.liga.cargodistributor.bot.serviceImpls.readcargo.ReadCargoCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.readcargo.ReadCargoVansCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.readcargo.ReadCargoVansReadingFileErrorCommandHandlerService;
import ru.liga.cargodistributor.cargo.repository.CargoItemTypeRepository;
import ru.liga.cargodistributor.cargo.repository.CargoVanTypeRepository;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.List;

@Service
public abstract class CommandHandlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandlerService.class);

    public static final String RESULT_JSON_FILE_NAME = "loadedVans.json";

    //todo: надо выпилить протекшие абстракции, бОльшая часть хендлеров эти сервисы не использует (кроме botservice)
    protected final TelegramClient telegramClient;
    protected final CargoDistributorBotService botService;
    protected final CargoConverterService cargoConverterService;
    protected final FileService fileService;

    @Autowired
    protected CommandHandlerService(@Value("${bot.token}") String token, @Value("${cache.capacity}") int cacheCapacity) {
        this.telegramClient = new OkHttpTelegramClient(token);
        this.botService = new CargoDistributorBotService(cacheCapacity);
        this.cargoConverterService = new CargoConverterService();
        this.fileService = new FileService();
    }

    public CommandHandlerService(TelegramClient telegramClient, CargoDistributorBotService botService, CargoConverterService cargoConverterService, FileService fileService) {
        this.telegramClient = telegramClient;
        this.botService = botService;
        this.cargoConverterService = cargoConverterService;
        this.fileService = fileService;
    }

    public abstract List<PartialBotApiMethod<Message>> processCommandAndGetResponseMessages(Update update);

    public static CommandHandlerService determineAndGetCommandHandler(
            Update update,
            CargoDistributorBotService botService,
            SendMessage lastSendMessage,
            TelegramClient telegramClient,
            CargoConverterService cargoConverterService,
            FileService fileService,
            CargoItemTypeRepository cargoItemTypeRepository,
            CargoVanTypeRepository cargoVanTypeRepository
    ) {
        CommandHandlerService handlerService;
        if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotUserCommand.START.getCommandText())
        ) {
            handlerService = new StartCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.DISTRIBUTE_BY_SELECTED_TYPES.getButtonText())
        ) {
            //todo: add tests for this scenario
            handlerService = new DistributeByTypesStartCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ENTER_CARGO_VAN_TYPE_NAME.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new DistributeByTypesProcessCargoVanTypeNameCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    cargoVanTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ENTER_CARGO_ITEM_TYPE_NAME.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new DistributeByTypesProcessCargoTypeNameCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    cargoItemTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_CARGO_ITEM_TYPE_WITH_SUCH_LEGEND_ALREADY_ADDED.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new DistributeByTypesProcessLegendCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ENTER_CARGO_ITEM_TYPE_COUNT.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new DistributeByTypesProcessCargoItemTypeCountCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.DISTRIBUTE_BY_TYPES_ADD_ONE_MORE_CARGO_TYPE.getButtonText()) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ADD_MORE_CARGO_TYPE_OR_CONTINUE.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new DistributeByTypesAddOneMoreCargoItemTypeCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.DISTRIBUTE_BY_TYPES_SELECT_ALGORITHM.getButtonText()) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ADD_MORE_CARGO_TYPE_OR_CONTINUE.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new DistributeByTypesPickAlgorithmCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_PICK_ALGORITHM.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new DistributeByTypesProcessAlgorithmCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.DISTRIBUTE_BY_TYPES_ENTER_VAN_LIMIT.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new DistributeByTypesProcessVanLimitAndRunDistributionCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.ADD_CARGO_VAN_TYPE.getButtonText())
        ) {
            //todo: add tests for this scenario
            handlerService = new AddCargoVanTypeCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_TYPE_NAME.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new AddCargoVanTypeEnterNameCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    cargoVanTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_TYPE_WIDTH.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new AddCargoVanTypeEnterWidthCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_TYPE_LENGTH.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new AddCargoVanTypeEnterLengthCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    cargoVanTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.EDIT_CARGO_VAN_TYPE.getButtonText())
        ) {
            //todo: add tests for this scenario
            handlerService = new EditCargoVanTypeCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.EDIT_CARGO_VAN_ENTER_CARGO_VAN_TYPE_NAME.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new EditCargoVanTypePickParameterCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    cargoVanTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.EDIT_CARGO_VAN_TYPE_NAME.getButtonText()) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.EDIT_CARGO_VAN_TYPE_PICK_PARAMETER.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new EditCargoVanTypeEnterNameCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_NEW_CARGO_VAN_TYPE_NAME.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new EditCargoVanTypeProcessNameCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    cargoVanTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.EDIT_CARGO_VAN_TYPE_WIDTH.getButtonText()) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.EDIT_CARGO_VAN_TYPE_PICK_PARAMETER.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new EditCargoVanTypeEnterWidthCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_NEW_CARGO_VAN_TYPE_WIDTH.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new EditCargoVanTypeProcessWidthCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.EDIT_CARGO_VAN_TYPE_LENGTH.getButtonText()) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.EDIT_CARGO_VAN_TYPE_PICK_PARAMETER.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new EditCargoVanTypeEnterLengthCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_NEW_CARGO_VAN_TYPE_LENGTH.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new EditCargoVanTypeProcessLengthCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.EDIT_CARGO_VAN_TYPE_SAVE_CHANGES.getButtonText())
        ) {
            //todo: add tests for this scenario
            handlerService = new EditCargoVanTypeSaveChangesCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    cargoVanTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.DELETE_CARGO_VAN_TYPE.getButtonText())
        ) {
            //todo: add tests for this scenario
            handlerService = new DeleteCargoVanTypeCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_TYPE_NAME_TO_DELETE.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new DeleteCargoVanTypeEnterNameCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    cargoVanTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.GET_ALL_CARGO_VAN_TYPES.getButtonText())
        ) {
            //todo: add tests for this scenario
            handlerService = new GetAllCargoVanTypesCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    cargoVanTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.GET_ALL_CARGO_TYPES.getButtonText())
        ) {
            //todo: add tests for this scenario
            handlerService = new GetAllCargoTypesCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    cargoItemTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.ADD_CARGO_TYPE.getButtonText())
        ) {
            //todo: add tests for this scenario
            handlerService = new AddCargoTypeCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_CARGO_TYPE_NAME.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new AddCargoTypeEnterNameCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    cargoItemTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_CARGO_TYPE_LEGEND.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new AddCargoTypeEnterLegendCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                !updateHasMessageText(update) &&
                        update.getMessage().hasDocument() &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.SEND_FILE_WITH_SINGLE_CARGO.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = parseUpdateDocumentAndDetermineCommandHandler(
                    "AddCargoType",
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
            handlerService = new EditCargoTypeCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.EDIT_CARGO_ENTER_CARGO_TYPE_NAME.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new EditCargoTypePickParameterCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    cargoItemTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.EDIT_CARGO_TYPE_NAME.getButtonText()) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.EDIT_CARGO_TYPE_PICK_PARAMETER.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new EditCargoTypeEnterNameCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_NEW_CARGO_TYPE_NAME.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new EditCargoTypeProcessNameCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    cargoItemTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.EDIT_CARGO_TYPE_LEGEND.getButtonText()) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.EDIT_CARGO_TYPE_PICK_PARAMETER.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new EditCargoTypeEnterLegendCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_NEW_CARGO_TYPE_LEGEND.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new EditCargoTypeProcessLegendCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.EDIT_CARGO_TYPE_SHAPE.getButtonText()) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.EDIT_CARGO_TYPE_PICK_PARAMETER.getMessageText(), lastSendMessage)
        ) {
            //todo: add tests for this scenario
            handlerService = new EditCargoTypeEnterShapeCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService
            );
        } else if (
                !updateHasMessageText(update) &&
                        update.getMessage().hasDocument() &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.SEND_FILE_WITH_SINGLE_CARGO_NEW_SHAPE.getMessageText(), lastSendMessage)

        ) {
            //todo: add tests for this scenario
            handlerService = parseUpdateDocumentAndDetermineCommandHandler(
                    "EditCargoTypeProcessNewShape",
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
            handlerService = new EditCargoTypeSaveChangesCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    cargoItemTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotKeyboardButton.DELETE_CARGO_TYPE.getButtonText())
        ) {
            handlerService = new DeleteCargoTypeCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_CARGO_TYPE_NAME_TO_DELETE.getMessageText(), lastSendMessage)
        ) {
            handlerService = new DeleteCargoTypeEnterNameCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    cargoItemTypeRepository
            );
        } else if (
                updateHasMessageText(update) &&
                        (
                                isUpdateMessageTextEqualTo(update, CargoDistributorBotResponseMessage.DISTRIBUTE_CARGO.getMessageText()) ||
                                        isUpdateMessageTextEqualTo(update, CargoDistributorBotUserCommand.DISTRIBUTE.getCommandText())
                        )
        ) {
            handlerService = new DistributeCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else if (
                updateHasMessageText(update) &&
                        (
                                isUpdateMessageTextEqualTo(update, CargoDistributorBotResponseMessage.READ_JSON_WITH_LOADED_VANS.getMessageText()) ||
                                        isUpdateMessageTextEqualTo(update, CargoDistributorBotUserCommand.READ_CARGO.getCommandText())
                        )
        ) {
            handlerService = new ReadCargoCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else if (
                !updateHasMessageText(update) &&
                        update.getMessage().hasDocument() &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.SEND_FILE_WITH_CARGO.getMessageText(), lastSendMessage)
        ) {
            handlerService = parseUpdateDocumentAndDetermineCommandHandler(
                    "ProcessCargoList",
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
            handlerService = new ReadVanLimitCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else if (
                updateHasMessageText(update) &&
                        isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.PICK_ALGORITHM.getMessageText(), lastSendMessage)
        ) {
            handlerService = new PickAlgorithmCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else if (
                isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.SEND_LOADED_VANS_TO_READ.getMessageText(), lastSendMessage) &&
                        (
                                updateHasMessageText(update) ||
                                        update.getMessage().hasDocument()
                        )
        ) {
            if (update.getMessage().hasDocument()) {
                return parseUpdateDocumentAndDetermineCommandHandler(
                        "ReadCargoVans",
                        update,
                        telegramClient,
                        botService,
                        cargoConverterService,
                        fileService,
                        cargoItemTypeRepository
                );
            }

            handlerService = new ReadCargoVansCommandHandlerService(
                    telegramClient,
                    botService,
                    cargoConverterService,
                    fileService,
                    null
            );

        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotUserCommand.HELP.getCommandText())
        ) {
            handlerService = new HelpCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else if (
                updateHasMessageText(update) &&
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotUserCommand.ABOUT.getCommandText())
        ) {
            handlerService = new AboutCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else {
            handlerService = new UnknownCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
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

    private static CommandHandlerService parseUpdateDocumentAndDetermineCommandHandler(
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
            case "ProcessCargoList": {
                if (errorMessage == null) {
                    handlerService = new ProcessCargoListCommandHandlerService(telegramClient,
                            botService,
                            cargoConverterService,
                            fileService,
                            docContent
                    );
                } else {
                    handlerService = new ProcessCargoListReadingFileErrorCommandHandlerService(
                            telegramClient,
                            botService,
                            cargoConverterService,
                            fileService,
                            errorMessage
                    );
                }

            }
            break;
            case "ReadCargoVans": {
                if (errorMessage == null) {
                    handlerService = new ReadCargoVansCommandHandlerService(
                            telegramClient,
                            botService,
                            cargoConverterService,
                            fileService,
                            docContent
                    );
                } else {
                    handlerService = new ReadCargoVansReadingFileErrorCommandHandlerService(
                            telegramClient,
                            botService,
                            cargoConverterService,
                            fileService,
                            errorMessage
                    );
                }
            }
            break;
            case "AddCargoType": {
                if (errorMessage == null) {
                    handlerService = new AddCargoTypeProcessSingleCargoCommandHandlerService(
                            telegramClient,
                            botService,
                            cargoConverterService,
                            fileService,
                            cargoItemTypeRepository,
                            docContent
                    );
                } else {
                    handlerService = new AddCargoTypeProcessSingleCargoReadingFileErrorCommandHandlerService(
                            telegramClient,
                            botService,
                            cargoConverterService,
                            fileService,
                            errorMessage
                    );
                }
            }
            break;
            case "EditCargoTypeProcessNewShape": {
                if (errorMessage == null) {
                    handlerService = new EditCargoTypeProcessNewShapeCommandHandlerService(
                            telegramClient,
                            botService,
                            cargoConverterService,
                            fileService,
                            docContent
                    );
                } else {
                    handlerService = new EditCargoTypeProcessNewShapeReadingFileErrorCommandHandlerService(
                            telegramClient,
                            botService,
                            cargoConverterService,
                            fileService,
                            errorMessage
                    );
                }
            }
            break;
            default: {
                LOGGER.info("parseUpdateDocumentAndDetermineCommandHandler: Unknown handler name: {}", handlerName);
                errorMessage += "\nUnknown handler name: " + handlerName;

                handlerService = new FileReadErrorUnknownCommandHandlerService(
                        telegramClient,
                        botService,
                        cargoConverterService,
                        fileService,
                        errorMessage
                );
            }
        }
        return handlerService;
    }
}
