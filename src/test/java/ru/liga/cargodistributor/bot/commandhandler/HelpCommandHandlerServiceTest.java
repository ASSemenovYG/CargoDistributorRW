package ru.liga.cargodistributor.bot.commandhandler;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.bot.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.CargoDistributorBotUserCommand;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HelpCommandHandlerServiceTest {

    @Test
    void processCommandAndGetResponseMessages() {
        CommandHandlerService handlerService = new HelpCommandHandlerService("token", 10);

        Chat chat = new Chat(123L, "private");

        Message message = new Message();
        message.setText(CargoDistributorBotUserCommand.HELP.getCommandText());
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        List<Object> resultResponse = handlerService.processCommandAndGetResponseMessages(update);
        assertThat(resultResponse.size()).isEqualTo(8);

        int textMessagesCount = (int) resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .count();

        assertThat(textMessagesCount).isEqualTo(5);

        int docMessagesCount = (int) resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendDocument.class))
                .count();

        assertThat(docMessagesCount).isEqualTo(2);

        int stickerMessagesCount = (int) resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendSticker.class))
                .count();

        assertThat(stickerMessagesCount).isEqualTo(1);

        List<String> textMessages = resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .map(msg -> ((SendMessage) msg).getText())
                .toList();

        assertThat(textMessages).containsExactly(
                CargoDistributorBotResponseMessage.HELP_DISTRIBUTE_COMMAND_DESCRIPTION.getMessageText(),
                CargoDistributorBotResponseMessage.HELP_DISTRIBUTE_COMMAND_RUN.getMessageText(),
                CargoDistributorBotResponseMessage.HELP_READCARGO_COMMAND_DESCRIPTION.getMessageText(),
                CargoDistributorBotResponseMessage.HELP_READCARGO_COMMAND_RUN.getMessageText(),
                CargoDistributorBotResponseMessage.RETURNING_TO_START.getMessageText()
        );
    }
}