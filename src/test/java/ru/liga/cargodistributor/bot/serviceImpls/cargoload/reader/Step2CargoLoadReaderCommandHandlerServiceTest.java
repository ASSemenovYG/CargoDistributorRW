package ru.liga.cargodistributor.bot.serviceImpls.cargoload.reader;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Step2CargoLoadReaderCommandHandlerServiceTest {
    private final CargoDistributorBotService botService = new CargoDistributorBotService(10);
    private final FileService fileService = new FileService(true);

    @Test
    void processCommandAndGetResponseMessages_validFile() {
        String jsonContent = fileService.readFromFileByPath("src/test/resources/test_loaded_vans.json");

        CommandHandlerService handlerService = new Step2CargoLoadReaderCommandHandlerService(
                new OkHttpTelegramClient("token"),
                botService,
                new CargoConverterService(),
                fileService,
                jsonContent
        );

        Chat chat = new Chat(123L, "private");

        Message message = new Message();
        message.setText("whatever");
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        List<PartialBotApiMethod<Message>> resultResponse = handlerService.processCommandAndGetResponseMessages(update);

        assertThat(resultResponse.size()).isEqualTo(7);

        int textMessagesCount = (int) resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .count();

        assertThat(textMessagesCount).isEqualTo(7);

        List<String> textMessages = resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .map(msg -> ((SendMessage) msg).getText())
                .toList();

        assertThat(textMessages).containsExactly(
                CargoDistributorBotResponseMessage.NUMBER_OF_READ_VANS.getMessageText() + "3",
                CargoDistributorBotResponseMessage.DISTRIBUTION_OF_CARGO_FROM_VANS.getMessageText(),
                """
                        ```
                        +      +
                        +      +
                        +666   +
                        +666   +
                        +4444  +
                        +55555 +
                        ++++++++

                        +      +
                        +44    +
                        +44    +
                        +999   +
                        +999   +
                        +999   +
                        ++++++++

                        +      +
                        +      +
                        +1     +
                        +66    +
                        +66    +
                        +66    +
                        ++++++++
                        ```""",
                CargoDistributorBotResponseMessage.CARGO_LIST_FROM_VANS.getMessageText(),
                """
                        ```
                        55555

                        4444

                        666
                        666

                        999
                        999
                        999

                        44
                        44

                        66
                        66
                        66

                        1
                        ```""",
                CargoDistributorBotResponseMessage.NUMBER_OF_ITEMS_FROM_VANS.getMessageText() + "7",
                CargoDistributorBotResponseMessage.RETURNING_TO_START.getMessageText()
        );
    }

    @Test
    void processCommandAndGetResponseMessages_validJsonFromMessage() {
        String jsonContent = fileService.readFromFileByPath("src/test/resources/test_loaded_vans.json");

        CommandHandlerService handlerService = new Step2CargoLoadReaderCommandHandlerService(
                new OkHttpTelegramClient("token"),
                botService,
                new CargoConverterService(),
                fileService,
                null
        );

        Chat chat = new Chat(123L, "private");

        Message message = new Message();
        message.setText(jsonContent);
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        List<PartialBotApiMethod<Message>> resultResponse = handlerService.processCommandAndGetResponseMessages(update);

        assertThat(resultResponse.size()).isEqualTo(7);

        int textMessagesCount = (int) resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .count();

        assertThat(textMessagesCount).isEqualTo(7);

        List<String> textMessages = resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .map(msg -> ((SendMessage) msg).getText())
                .toList();

        assertThat(textMessages).containsExactly(
                CargoDistributorBotResponseMessage.NUMBER_OF_READ_VANS.getMessageText() + "3",
                CargoDistributorBotResponseMessage.DISTRIBUTION_OF_CARGO_FROM_VANS.getMessageText(),
                """
                        ```
                        +      +
                        +      +
                        +666   +
                        +666   +
                        +4444  +
                        +55555 +
                        ++++++++

                        +      +
                        +44    +
                        +44    +
                        +999   +
                        +999   +
                        +999   +
                        ++++++++

                        +      +
                        +      +
                        +1     +
                        +66    +
                        +66    +
                        +66    +
                        ++++++++
                        ```""",
                CargoDistributorBotResponseMessage.CARGO_LIST_FROM_VANS.getMessageText(),
                """
                        ```
                        55555

                        4444

                        666
                        666

                        999
                        999
                        999

                        44
                        44

                        66
                        66
                        66

                        1
                        ```""",
                CargoDistributorBotResponseMessage.NUMBER_OF_ITEMS_FROM_VANS.getMessageText() + "7",
                CargoDistributorBotResponseMessage.RETURNING_TO_START.getMessageText()
        );
    }

    @Test
    void processCommandAndGetResponseMessages_invalidContent() {
        CommandHandlerService handlerService = new Step2CargoLoadReaderCommandHandlerService(
                new OkHttpTelegramClient("token"),
                botService,
                new CargoConverterService(),
                fileService,
                "expectedToGetJsonHere"
        );

        Chat chat = new Chat(123L, "private");

        Message message = new Message();
        message.setText("whatever");
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        List<PartialBotApiMethod<Message>> resultResponse = handlerService.processCommandAndGetResponseMessages(update);

        assertThat(resultResponse.size()).isEqualTo(3);

        int textMessagesCount = (int) resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .count();

        assertThat(textMessagesCount).isEqualTo(3);

        List<String> textMessages = resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .map(msg -> ((SendMessage) msg).getText())
                .toList();

        assertThat(textMessages).contains(
                CargoDistributorBotResponseMessage.ERROR_WHILE_PROCESSING_CARGO_VAN_JSON_MESSAGE.getMessageText(),
                CargoDistributorBotResponseMessage.RETURNING_TO_START.getMessageText()
        );
    }
}