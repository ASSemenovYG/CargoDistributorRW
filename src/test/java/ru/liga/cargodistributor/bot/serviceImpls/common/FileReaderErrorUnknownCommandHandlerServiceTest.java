package ru.liga.cargodistributor.bot.serviceImpls.common;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FileReaderErrorUnknownCommandHandlerServiceTest {

    @Test
    void processCommand_WithLastMessage() {
        CargoDistributorBotService botService = new CargoDistributorBotService(10);

        long chatId = 123L;
        String errorMessage = "a guy with dyslexia walks into a bra";

        String lastMessageText = "autocorrect always mess up my joke lunches";

        SendMessage lastMessage = new SendMessage(String.valueOf(chatId), lastMessageText);

        botService.putLastMessageToCache(String.valueOf(chatId), lastMessage);

        CommandHandlerService handlerService = new FileReaderErrorUnknownCommandHandlerService(
                botService,
                errorMessage
        );

        Chat chat = new Chat(chatId, "private");

        Message message = new Message();
        message.setText("Good luck with that!");
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        List<PartialBotApiMethod<Message>> resultResponse = handlerService.processCommand(update);

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
                CargoDistributorBotResponseMessage.ERROR_WHILE_READING_FROM_FILE_MESSAGE.getMessageText(),
                "```" + errorMessage + "```",
                CargoDistributorBotResponseMessage.ERROR_WHILE_READING_FROM_FILE_FOUND_PREVIOUS_RESPONSE.getMessageText(),
                lastMessageText
        );
    }

    @Test
    void processCommand_WithoutLastMessage() {
        long chatId = 123L;
        String errorMessage = "a guy with dyslexia walks into a bra";

        CommandHandlerService handlerService = new FileReaderErrorUnknownCommandHandlerService(
                new CargoDistributorBotService(10),
                errorMessage
        );

        Chat chat = new Chat(chatId, "private");

        Message message = new Message();
        message.setText("Good luck with that!");
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        List<PartialBotApiMethod<Message>> resultResponse = handlerService.processCommand(update);

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
                CargoDistributorBotResponseMessage.ERROR_WHILE_READING_FROM_FILE_MESSAGE.getMessageText(),
                "```" + errorMessage + "```"
        );
    }
}