package ru.liga.cargodistributor.bot.serviceImpls.distributefromfile;

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

class ReadVanLimitCommandHandlerServiceTest {

    @Test
    void processCommandAndGetResponseMessages_validInteger() {
        CargoDistributorBotService botService = new CargoDistributorBotService(10);

        long chatId = 123L;
        int vanLimit = 42;

        CommandHandlerService handlerService = new ReadVanLimitCommandHandlerService(
                new OkHttpTelegramClient("token"),
                botService,
                new CargoConverterService(),
                new FileService(true)
        );

        Chat chat = new Chat(chatId, "private");

        Message message = new Message();
        message.setText(String.valueOf(vanLimit));
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        List<PartialBotApiMethod<Message>> resultResponse = handlerService.processCommandAndGetResponseMessages(update);

        assertThat(vanLimit).isEqualTo(botService.getVanLimitFromCache(String.valueOf(chatId)));

        assertThat(resultResponse.size()).isEqualTo(1);

        int textMessagesCount = (int) resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .count();

        assertThat(textMessagesCount).isEqualTo(1);

        List<String> textMessages = resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .map(msg -> ((SendMessage) msg).getText())
                .toList();

        assertThat(textMessages).containsExactly(
                CargoDistributorBotResponseMessage.PICK_ALGORITHM.getMessageText()
        );
    }

    @Test
    void processCommandAndGetResponseMessages_invalidInteger() {
        long chatId = 123L;

        CommandHandlerService handlerService = new ReadVanLimitCommandHandlerService(
                new OkHttpTelegramClient("token"),
                new CargoDistributorBotService(10),
                new CargoConverterService(),
                new FileService(true)
        );

        Chat chat = new Chat(chatId, "private");

        Message message = new Message();
        message.setText("three hundred vans");
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
                CargoDistributorBotResponseMessage.FAILED_TO_PARSE_INTEGER.getMessageText(),
                CargoDistributorBotResponseMessage.ENTER_CARGO_VAN_LIMIT.getMessageText()
        );
    }
}