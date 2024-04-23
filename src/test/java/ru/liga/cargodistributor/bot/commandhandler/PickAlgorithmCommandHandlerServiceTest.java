package ru.liga.cargodistributor.bot.commandhandler;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.algorithm.DistributionAlgorithmName;
import ru.liga.cargodistributor.bot.CargoDistributorBotResponseMessage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PickAlgorithmCommandHandlerServiceTest {

    //todo: add more tests
    @Test
    void processCommandAndGetResponseMessages_cargoListNotFound() {
        CommandHandlerService handlerService = new PickAlgorithmCommandHandlerService("token", 10);

        Chat chat = new Chat(123L, "private");

        Message message = new Message();
        message.setText(DistributionAlgorithmName.SIMPLE_FIT.getTitle());
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        List<Object> resultResponse = handlerService.processCommandAndGetResponseMessages(update);
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
                CargoDistributorBotResponseMessage.FAILED_TO_FIND_CARGO_LIST.getMessageText(),
                CargoDistributorBotResponseMessage.SEND_FILE_WITH_CARGO.getMessageText()
        );
    }
}