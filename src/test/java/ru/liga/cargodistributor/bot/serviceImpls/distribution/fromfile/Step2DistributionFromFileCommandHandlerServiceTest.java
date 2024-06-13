package ru.liga.cargodistributor.bot.serviceImpls.distribution.fromfile;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.serviceImpls.distibution.fromfile.Step2DistributionFromFileCommandHandlerService;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.cargo.CargoItem;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.util.services.FileService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Step2DistributionFromFileCommandHandlerServiceTest {
    private final CargoDistributorBotService botService = new CargoDistributorBotService(10);
    private final FileService fileService = new FileService(true);

    @Test
    void processCommandAndGetResponseMessages_validFile() {
        List<CargoItem> cargoItems = new ArrayList<>();
        cargoItems.add(new CargoItem(9, 3, 3));
        cargoItems.add(new CargoItem(6, 2, 3));
        cargoItems.add(new CargoItem(5, 1, 5));
        cargoItems.add(new CargoItem(1, 1, 1));
        cargoItems.add(new CargoItem(1, 1, 1));
        cargoItems.add(new CargoItem(3, 1, 3));
        CargoItemList cargoItemList = new CargoItemList(cargoItems);

        String cargoContent = fileService.readFromFileByPath("src/test/resources/test_valid_cargo_file.pkg");

        CommandHandlerService handlerService = new Step2DistributionFromFileCommandHandlerService(
                botService,
                new CargoConverterService(),
                cargoContent
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

        assertThat(textMessages).containsExactly(
                CargoDistributorBotResponseMessage.FOUND_CARGO_ITEMS_IN_A_FILE.getMessageText(),
                "```" + cargoItemList.getCargoItemNamesAsString() + "```",
                CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_LIMIT.getMessageText()
        );
    }

    @Test
    void processCommandAndGetResponseMessages_emptyContent() {
        CommandHandlerService handlerService = new Step2DistributionFromFileCommandHandlerService(
                botService,
                new CargoConverterService(),
                ""
        );

        Chat chat = new Chat(123L, "private");

        Message message = new Message();
        message.setText("whatever");
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        List<PartialBotApiMethod<Message>> resultResponse = handlerService.processCommandAndGetResponseMessages(update);

        assertThat(resultResponse.size()).isEqualTo(2);

        int textMessagesCount = (int) resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .count();

        assertThat(textMessagesCount).isEqualTo(2);

        List<String> textMessages = resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .map(msg -> ((SendMessage) msg).getText())
                .toList();

        assertThat(textMessages).containsExactly(
                CargoDistributorBotResponseMessage.NO_CARGO_ITEMS_FOUND_IN_A_FILE.getMessageText(),
                CargoDistributorBotResponseMessage.RETURNING_TO_START.getMessageText()
        );
    }

    @Test
    void processCommandAndGetResponseMessages_invalidFile_ParsingError() {
        String cargoContent = fileService.readFromFileByPath("src/test/resources/test_cargo_file_with_invalid_item.pkg");

        CommandHandlerService handlerService = new Step2DistributionFromFileCommandHandlerService(
                botService,
                new CargoConverterService(),
                cargoContent
        );

        Chat chat = new Chat(123L, "private");

        Message message = new Message();
        message.setText("whatever");
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        List<PartialBotApiMethod<Message>> resultResponse = handlerService.processCommandAndGetResponseMessages(update);

        assertThat(resultResponse.size()).isEqualTo(4);

        int textMessagesCount = (int) resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .count();

        assertThat(textMessagesCount).isEqualTo(4);

        List<String> textMessages = resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .map(msg -> ((SendMessage) msg).getText())
                .toList();

        assertThat(textMessages).containsExactly(
                CargoDistributorBotResponseMessage.ERROR_WHILE_PROCESSING_CARGO_FILE.getMessageText(),
                """
                        ```Во входных данных обнаружена невалидная посылка:
                        777
                        777
                        77
                        Посылки могут быть только прямоугольными.
                        Некорректные параметры посылки, размер 7 не соответствует длине 3 и ширине 3
                        ```""",
                CargoDistributorBotResponseMessage.TRY_AGAIN.getMessageText(),
                CargoDistributorBotResponseMessage.SEND_FILE_WITH_CARGO.getMessageText()
        );
    }
}