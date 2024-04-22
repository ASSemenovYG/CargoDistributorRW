package ru.liga.cargodistributor.bot.commandhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.bot.CargoDistributorBotKeyboard;
import ru.liga.cargodistributor.bot.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.CargoDistributorBotUserCommand;
import ru.liga.cargodistributor.cargo.CargoConverterService;
import ru.liga.cargodistributor.util.FileService;

import java.util.List;

@Service
public abstract class CommandHandlerService {
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

    public abstract List<Object> processCommandAndGetResponseMessages(Update update);

    public static CommandHandlerService determineAndGetCommandHandler(Update update, CargoDistributorBotService botService, SendMessage lastSendMessage, TelegramClient telegramClient, CargoConverterService cargoConverterService, FileService fileService) {
        CommandHandlerService handlerService;
        if ((updateHasMessageText(update) && isUpdateMessageTextEqualTo(update, CargoDistributorBotUserCommand.START.getCommandText()))) {
            handlerService = new StartCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else if (updateHasMessageText(update) &&
                (
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotResponseMessage.DISTRIBUTE_CARGO.getMessageText()) ||
                                isUpdateMessageTextEqualTo(update, CargoDistributorBotUserCommand.DISTRIBUTE.getCommandText())
                )
        ) {
            handlerService = new DistributeCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else if (updateHasMessageText(update) &&
                (
                        isUpdateMessageTextEqualTo(update, CargoDistributorBotResponseMessage.READ_JSON_WITH_LOADED_VANS.getMessageText()) ||
                                isUpdateMessageTextEqualTo(update, CargoDistributorBotUserCommand.READ_CARGO.getCommandText())
                )
        ) {
            handlerService = new ReadCargoCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else if (!updateHasMessageText(update) &&
                update.getMessage().hasDocument() &&
                isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.SEND_FILE_WITH_CARGO.getMessageText(), lastSendMessage)
        ) {
            handlerService = new ProcessCargoListCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else if (updateHasMessageText(update) &&
                isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.ENTER_VAN_LIMIT.getMessageText(), lastSendMessage)
        ) {
            handlerService = new ReadVanLimitCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else if (updateHasMessageText(update) &&
                isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.PICK_ALGORITHM.getMessageText(), lastSendMessage)
        ) {
            handlerService = new PickAlgorithmCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else if (isLastSendMessageEqualTo(CargoDistributorBotResponseMessage.SEND_LOADED_VANS_TO_READ.getMessageText(), lastSendMessage) &&
                (
                        updateHasMessageText(update) ||
                                update.getMessage().hasDocument()
                )
        ) {
            handlerService = new ReadCargoVansCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else if (updateHasMessageText(update) &&
                isUpdateMessageTextEqualTo(update, CargoDistributorBotUserCommand.HELP.getCommandText())
        ) {
            handlerService = new HelpCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else if (updateHasMessageText(update) &&
                isUpdateMessageTextEqualTo(update, CargoDistributorBotUserCommand.ABOUT.getCommandText())
        ) {
            handlerService = new AboutCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        } else {
            handlerService = new UnknownCommandHandlerService(telegramClient, botService, cargoConverterService, fileService);
        }
        return handlerService;
    }

    protected void returnToStart(long chatId, List<Object> resultResponse) {
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
}
