package ru.liga.cargodistributor.bot.services;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.liga.cargodistributor.algorithm.enums.DistributionAlgorithmName;
import ru.liga.cargodistributor.bot.CargoDistributorBot;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotUserCommand;
import ru.liga.cargodistributor.bot.serviceImpls.common.AboutCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.common.HelpCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.common.StartCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.common.UnknownCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.distributefromfile.*;
import ru.liga.cargodistributor.bot.serviceImpls.readcargo.ReadCargoCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.readcargo.ReadCargoVansCommandHandlerService;
import ru.liga.cargodistributor.bot.serviceImpls.readcargo.ReadCargoVansReadingFileErrorCommandHandlerService;
import ru.liga.cargodistributor.cargo.repository.CargoItemTypeRepository;
import ru.liga.cargodistributor.cargo.repository.CargoVanTypeRepository;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
class CommandHandlerServiceTest {
    //todo: придумать как замокать эти тесты так, чтобы они работали без поднятой БД

    @Autowired
    private CargoDistributorBot cargoDistributorBot;

    @Autowired
    CargoItemTypeRepository cargoItemTypeRepository;

    @Autowired
    CargoVanTypeRepository cargoVanTypeRepository;

    private final TelegramClient telegramClient = new OkHttpTelegramClient("token");
    private final CargoDistributorBotService botService = new CargoDistributorBotService(10);
    private final CargoConverterService cargoConverterService = new CargoConverterService();
    private final FileService fileService = new FileService(true);

    @Test
    void determineAndGetCommandHandler_Start() {
        Chat chat = new Chat(123L, "private");

        Message message = new Message();
        message.setText(CargoDistributorBotUserCommand.START.getCommandText());
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        CommandHandlerService handlerService = CommandHandlerService.determineAndGetCommandHandler(
                update,
                botService,
                null,
                telegramClient,
                cargoConverterService,
                fileService,
                cargoItemTypeRepository,
                cargoVanTypeRepository
        );

        assertThat(handlerService.getClass()).isEqualTo(StartCommandHandlerService.class);
    }

    @Test
    void determineAndGetCommandHandler_Distribute_FromMessage() {
        Chat chat = new Chat(123L, "private");

        Message message = new Message();
        message.setText(CargoDistributorBotResponseMessage.DISTRIBUTE_CARGO.getMessageText());
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        CommandHandlerService handlerService = CommandHandlerService.determineAndGetCommandHandler(
                update,
                botService,
                null,
                telegramClient,
                cargoConverterService,
                fileService,
                cargoItemTypeRepository,
                cargoVanTypeRepository
        );

        assertThat(handlerService.getClass()).isEqualTo(DistributeCommandHandlerService.class);
    }

    @Test
    void determineAndGetCommandHandler_Distribute_FromCommand() {
        Chat chat = new Chat(123L, "private");

        Message message = new Message();
        message.setText(CargoDistributorBotUserCommand.DISTRIBUTE.getCommandText());
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        CommandHandlerService handlerService = CommandHandlerService.determineAndGetCommandHandler(
                update,
                botService,
                null,
                telegramClient,
                cargoConverterService,
                fileService,
                cargoItemTypeRepository,
                cargoVanTypeRepository
        );

        assertThat(handlerService.getClass()).isEqualTo(DistributeCommandHandlerService.class);
    }

    @Test
    void determineAndGetCommandHandler_ReadCargo_FromText() {
        Chat chat = new Chat(123L, "private");

        Message message = new Message();
        message.setText(CargoDistributorBotResponseMessage.READ_JSON_WITH_LOADED_VANS.getMessageText());
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        CommandHandlerService handlerService = CommandHandlerService.determineAndGetCommandHandler(
                update,
                botService,
                null,
                telegramClient,
                cargoConverterService,
                fileService,
                cargoItemTypeRepository,
                cargoVanTypeRepository
        );

        assertThat(handlerService.getClass()).isEqualTo(ReadCargoCommandHandlerService.class);
    }

    @Test
    void determineAndGetCommandHandler_ReadCargo_FromCommand() {
        Chat chat = new Chat(123L, "private");

        Message message = new Message();
        message.setText(CargoDistributorBotUserCommand.READ_CARGO.getCommandText());
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        CommandHandlerService handlerService = CommandHandlerService.determineAndGetCommandHandler(
                update,
                botService,
                null,
                telegramClient,
                cargoConverterService,
                fileService,
                cargoItemTypeRepository,
                cargoVanTypeRepository
        );

        assertThat(handlerService.getClass()).isEqualTo(ReadCargoCommandHandlerService.class);
    }

    @Test
    void determineAndGetCommandHandler_ProcessCargoList() {
        Chat chat = new Chat(123L, "private");

        SendMessage lastMessage = botService.buildTextMessageWithoutKeyboard(123L, CargoDistributorBotResponseMessage.SEND_FILE_WITH_CARGO.getMessageText());

        Document document = new Document(
                "BQACAgIAAxkBAAICImZBAAFaKNCP6NvuteJuYOQmUwX3EAAC0UsAAoCYCUqv4yRB0jnEYTUE",
                "AgAD0UsAAoCYCUo",
                null,
                "test_valid_cargo_file_3.pkg",
                "application/x-xar",
                156L
        );

        Message message = new Message();
        message.setChat(chat);
        message.setDocument(document);

        Update update = new Update();
        update.setMessage(message);

        CommandHandlerService handlerService = CommandHandlerService.determineAndGetCommandHandler(
                update,
                botService,
                lastMessage,
                new OkHttpTelegramClient(cargoDistributorBot.getBotToken()),
                cargoConverterService,
                fileService,
                cargoItemTypeRepository,
                cargoVanTypeRepository
        );

        assertThat(handlerService.getClass()).isEqualTo(ProcessCargoListCommandHandlerService.class);
    }

    @Test
    void determineAndGetCommandHandler_ProcessCargoListReadingFileError() {
        Chat chat = new Chat(123L, "private");

        SendMessage lastMessage = botService.buildTextMessageWithoutKeyboard(123L, CargoDistributorBotResponseMessage.SEND_FILE_WITH_CARGO.getMessageText());

        Document document = new Document();

        Message message = new Message();
        message.setChat(chat);
        message.setDocument(document);

        Update update = new Update();
        update.setMessage(message);

        CommandHandlerService handlerService = CommandHandlerService.determineAndGetCommandHandler(
                update,
                botService,
                lastMessage,
                telegramClient,
                cargoConverterService,
                fileService,
                cargoItemTypeRepository,
                cargoVanTypeRepository
        );

        assertThat(handlerService.getClass()).isEqualTo(ProcessCargoListReadingFileErrorCommandHandlerService.class);
    }

    @Test
    void determineAndGetCommandHandler_ReadVanLimit() {
        Chat chat = new Chat(123L, "private");

        SendMessage lastMessage = botService.buildTextMessageWithoutKeyboard(
                123L,
                CargoDistributorBotResponseMessage.ENTER_VAN_LIMIT.getMessageText()
        );

        Message message = new Message();
        message.setChat(chat);
        message.setText("42");

        Update update = new Update();
        update.setMessage(message);

        CommandHandlerService handlerService = CommandHandlerService.determineAndGetCommandHandler(
                update,
                botService,
                lastMessage,
                telegramClient,
                cargoConverterService,
                fileService,
                cargoItemTypeRepository,
                cargoVanTypeRepository
        );

        assertThat(handlerService.getClass()).isEqualTo(ReadVanLimitCommandHandlerService.class);
    }

    @Test
    void determineAndGetCommandHandler_PickAlgorithm() {
        Chat chat = new Chat(123L, "private");

        SendMessage lastMessage = botService.buildTextMessageWithoutKeyboard(
                123L,
                CargoDistributorBotResponseMessage.PICK_ALGORITHM.getMessageText()
        );

        Message message = new Message();
        message.setChat(chat);
        message.setText(DistributionAlgorithmName.SIMPLE_FIT.getTitle());

        Update update = new Update();
        update.setMessage(message);

        CommandHandlerService handlerService = CommandHandlerService.determineAndGetCommandHandler(
                update,
                botService,
                lastMessage,
                telegramClient,
                cargoConverterService,
                fileService,
                cargoItemTypeRepository,
                cargoVanTypeRepository
        );

        assertThat(handlerService.getClass()).isEqualTo(PickAlgorithmCommandHandlerService.class);
    }

    @Test
    void determineAndGetCommandHandler_ReadCargoVans_FromMessage() {
        Chat chat = new Chat(123L, "private");

        SendMessage lastMessage = botService.buildTextMessageWithoutKeyboard(
                123L,
                CargoDistributorBotResponseMessage.SEND_LOADED_VANS_TO_READ.getMessageText()
        );

        Message message = new Message();
        message.setChat(chat);
        message.setText("some json");

        Update update = new Update();
        update.setMessage(message);

        CommandHandlerService handlerService = CommandHandlerService.determineAndGetCommandHandler(
                update,
                botService,
                lastMessage,
                telegramClient,
                cargoConverterService,
                fileService,
                cargoItemTypeRepository,
                cargoVanTypeRepository
        );

        assertThat(handlerService.getClass()).isEqualTo(ReadCargoVansCommandHandlerService.class);
    }

    @Test
    void determineAndGetCommandHandler_ReadCargoVansReadingFileError() {
        Chat chat = new Chat(123L, "private");

        SendMessage lastMessage = botService.buildTextMessageWithoutKeyboard(
                123L,
                CargoDistributorBotResponseMessage.SEND_LOADED_VANS_TO_READ.getMessageText()
        );

        Message message = new Message();
        message.setDocument(new Document());
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        CommandHandlerService handlerService = CommandHandlerService.determineAndGetCommandHandler(
                update,
                botService,
                lastMessage,
                telegramClient,
                cargoConverterService,
                fileService,
                cargoItemTypeRepository,
                cargoVanTypeRepository
        );

        assertThat(handlerService.getClass()).isEqualTo(ReadCargoVansReadingFileErrorCommandHandlerService.class);
    }

    @Test
    void determineAndGetCommandHandler_ReadCargoVans_FromFile() {
        Chat chat = new Chat(123L, "private");

        SendMessage lastMessage = botService.buildTextMessageWithoutKeyboard(
                123L,
                CargoDistributorBotResponseMessage.SEND_LOADED_VANS_TO_READ.getMessageText()
        );

        Message message = new Message();
        message.setDocument(
                new Document(
                        "BQACAgIAAxkBAAIB-2ZA5TGOvTNcFv5XQIPl-L2bmfLPAAKCSgACgJgJShjPI7bBQQIBNQQ",
                        "AgADgkoAAoCYCUo",
                        null,
                        "loadedVansExample.json",
                        "application/json",
                        864L
                )
        );
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        CommandHandlerService handlerService = CommandHandlerService.determineAndGetCommandHandler(
                update,
                botService,
                lastMessage,
                new OkHttpTelegramClient(cargoDistributorBot.getBotToken()),
                cargoConverterService,
                fileService,
                cargoItemTypeRepository,
                cargoVanTypeRepository
        );

        assertThat(handlerService.getClass()).isEqualTo(ReadCargoVansCommandHandlerService.class);
    }

    @Test
    void determineAndGetCommandHandler_Help() {
        Chat chat = new Chat(123L, "private");

        Message message = new Message();
        message.setChat(chat);
        message.setText(CargoDistributorBotUserCommand.HELP.getCommandText());

        Update update = new Update();
        update.setMessage(message);

        CommandHandlerService handlerService = CommandHandlerService.determineAndGetCommandHandler(
                update,
                botService,
                null,
                telegramClient,
                cargoConverterService,
                fileService,
                cargoItemTypeRepository,
                cargoVanTypeRepository
        );

        assertThat(handlerService.getClass()).isEqualTo(HelpCommandHandlerService.class);
    }

    @Test
    void determineAndGetCommandHandler_About() {
        Chat chat = new Chat(123L, "private");

        Message message = new Message();
        message.setChat(chat);
        message.setText(CargoDistributorBotUserCommand.ABOUT.getCommandText());

        Update update = new Update();
        update.setMessage(message);

        CommandHandlerService handlerService = CommandHandlerService.determineAndGetCommandHandler(
                update,
                botService,
                null,
                telegramClient,
                cargoConverterService,
                fileService,
                cargoItemTypeRepository,
                cargoVanTypeRepository
        );

        assertThat(handlerService.getClass()).isEqualTo(AboutCommandHandlerService.class);
    }

    @Test
    void determineAndGetCommandHandler_Unknown() {
        Chat chat = new Chat(123L, "private");

        Message message = new Message();
        message.setChat(chat);
        message.setText("A dyslexic man walks into a bra...");

        Update update = new Update();
        update.setMessage(message);

        CommandHandlerService handlerService = CommandHandlerService.determineAndGetCommandHandler(
                update,
                botService,
                null,
                telegramClient,
                cargoConverterService,
                fileService,
                cargoItemTypeRepository,
                cargoVanTypeRepository
        );

        assertThat(handlerService.getClass()).isEqualTo(UnknownCommandHandlerService.class);
    }
}