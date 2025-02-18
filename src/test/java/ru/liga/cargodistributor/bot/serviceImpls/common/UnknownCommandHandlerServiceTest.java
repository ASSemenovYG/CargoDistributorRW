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

class UnknownCommandHandlerServiceTest {

    @Test
    void processCommand_withoutLastMessageInCache() {
        CommandHandlerService handlerService = new UnknownCommandHandlerService(
                new CargoDistributorBotService(10)
        );

        Chat chat = new Chat(123L, "private");

        Message message = new Message();
        message.setText("my pronouns are: 'he-he-he', because I identify myself as a Comedian");
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        List<PartialBotApiMethod<Message>> resultResponse = handlerService.processCommand(update);

        int textMessagesCount = (int) resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .count();

        assertThat(textMessagesCount).isEqualTo(2);

        List<String> textMessages = resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .map(msg -> ((SendMessage) msg).getText())
                .toList();

        assertThat(textMessages).containsExactly(
                CargoDistributorBotResponseMessage.CANT_PROCESS_LAST_MESSAGE.getMessageText(),
                CargoDistributorBotResponseMessage.RETURNING_TO_START.getMessageText()
        );
    }

    @Test
    void processCommand_withLastMessageInCache() {
        CargoDistributorBotService botService = new CargoDistributorBotService(10);

        long chatId = 123L;

        String lastMessageText = "autocorrect always mess up my joke lunches";

        SendMessage lastMessage = new SendMessage(String.valueOf(chatId), lastMessageText);

        botService.putLastMessageToCache(String.valueOf(chatId), lastMessage);

        CommandHandlerService handlerService = new UnknownCommandHandlerService(
                botService
        );

        Chat chat = new Chat(chatId, "private");

        Message message = new Message();
        message.setText("Good luck with that!");
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        List<PartialBotApiMethod<Message>> resultResponse = handlerService.processCommand(update);

        int textMessagesCount = (int) resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .count();

        assertThat(textMessagesCount).isEqualTo(2);

        List<String> textMessages = resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .map(msg -> ((SendMessage) msg).getText())
                .toList();

        assertThat(textMessages).containsExactly(
                CargoDistributorBotResponseMessage.CANT_PROCESS_LAST_MESSAGE_FOUND_PREVIOUS_RESPONSE.getMessageText(),
                lastMessageText
        );
    }
}