package ru.liga.cargodistributor.bot.serviceImpls.common;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotUserCommand;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AboutCommandHandlerServiceTest {

    @Test
    void processCommand() {
        CommandHandlerService handlerService = new AboutCommandHandlerService(
                new CargoDistributorBotService(10)
        );

        Chat chat = new Chat(123L, "private");

        Message message = new Message();
        message.setText(CargoDistributorBotUserCommand.ABOUT.getCommandText());
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        List<PartialBotApiMethod<Message>> resultResponse = handlerService.processCommand(update);
        assertThat(resultResponse.size()).isEqualTo(5);

        int textMessagesCount = (int) resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .count();

        assertThat(textMessagesCount).isEqualTo(1);

        int stickerMessagesCount = (int) resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendSticker.class))
                .count();

        assertThat(stickerMessagesCount).isEqualTo(3);

        int videoNoteMessagesCount = (int) resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendVideoNote.class))
                .count();

        assertThat(videoNoteMessagesCount).isEqualTo(1);

        List<String> textMessages = resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .map(msg -> ((SendMessage) msg).getText())
                .toList();

        assertThat(textMessages).containsExactly(
                CargoDistributorBotResponseMessage.RETURNING_TO_START.getMessageText()
        );
    }
}