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

class Step2CargoLoadReaderFileErrorCommandHandlerServiceTest {

    @Test
    void processCommandAndGetResponseMessages() {
        long chatId = 123L;
        String errorMessage = "a guy with dyslexia walks into a bra";

        CommandHandlerService handlerService = new Step2CargoLoadReaderFileErrorCommandHandlerService(
                new OkHttpTelegramClient("token"),
                new CargoDistributorBotService(10),
                new CargoConverterService(),
                new FileService(true),
                errorMessage
        );

        Chat chat = new Chat(chatId, "private");

        Message message = new Message();
        message.setText("Good luck with that!");
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
                CargoDistributorBotResponseMessage.ERROR_WHILE_PROCESSING_CARGO_VAN_FILE.getMessageText(),
                "```" + errorMessage + "```",
                CargoDistributorBotResponseMessage.RETURNING_TO_START.getMessageText()
        );
    }
}