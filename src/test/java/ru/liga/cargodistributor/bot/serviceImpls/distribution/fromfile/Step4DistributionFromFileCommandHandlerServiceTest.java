package ru.liga.cargodistributor.bot.serviceImpls.distribution.fromfile;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.liga.cargodistributor.algorithm.enums.DistributionAlgorithmName;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotResponseMessage;
import ru.liga.cargodistributor.bot.serviceImpls.distibution.fromfile.Step4DistributionFromFileCommandHandlerService;
import ru.liga.cargodistributor.bot.services.CargoDistributorBotService;
import ru.liga.cargodistributor.bot.services.CommandHandlerService;
import ru.liga.cargodistributor.cargo.CargoItem;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.services.CargoConverterService;
import ru.liga.cargodistributor.util.services.FileService;
import ru.liga.cargodistributor.util.services.FileServiceTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Step4DistributionFromFileCommandHandlerServiceTest {

    @AfterAll
    static void clearJsonVanTestDirectory() {
        FileServiceTest.clearJsonVanTestDirectory();
    }

    @Test
    void processCommandAndGetResponseMessages_cargoListNotFound() {
        CommandHandlerService handlerService = new Step4DistributionFromFileCommandHandlerService(
                new CargoDistributorBotService(10),
                new CargoConverterService(),
                new FileService(true)
        );

        Chat chat = new Chat(123L, "private");

        Message message = new Message();
        message.setText(DistributionAlgorithmName.SIMPLE_FIT.getTitle());
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
                CargoDistributorBotResponseMessage.FAILED_TO_FIND_CARGO_LIST.getMessageText(),
                CargoDistributorBotResponseMessage.SEND_FILE_WITH_CARGO.getMessageText()
        );
    }

    @Test
    void processCommandAndGetResponseMessages_cargoListFound() {
        CargoDistributorBotService botService = new CargoDistributorBotService(10);

        long chatId = 123L;

        List<CargoItem> cargoItems = new ArrayList<>();
        cargoItems.add(new CargoItem(1, 1, 1));
        cargoItems.add(new CargoItem(2, 1, 2));
        cargoItems.add(new CargoItem(3, 1, 3));
        CargoItemList cargoItemList = new CargoItemList(cargoItems);

        botService.putCargoItemListToCache(String.valueOf(chatId), cargoItemList);
        botService.putVanLimitToCache(String.valueOf(chatId), 10);

        CommandHandlerService handlerService = new Step4DistributionFromFileCommandHandlerService(
                botService,
                new CargoConverterService(),
                new FileService(true)
        );

        Chat chat = new Chat(chatId, "private");

        Message message = new Message();
        message.setText(DistributionAlgorithmName.SIMPLE_FIT.getTitle());
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        List<PartialBotApiMethod<Message>> resultResponse = handlerService.processCommandAndGetResponseMessages(update);

        assertThat(resultResponse.size()).isEqualTo(5);

        int textMessagesCount = (int) resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .count();

        assertThat(textMessagesCount).isEqualTo(4);

        int docMessagesCount = (int) resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendDocument.class))
                .count();

        assertThat(docMessagesCount).isEqualTo(1);

        List<String> textMessages = resultResponse.stream()
                .filter(msg -> msg.getClass().equals(SendMessage.class))
                .map(msg -> ((SendMessage) msg).getText())
                .toList();

        assertThat(textMessages).contains(
                CargoDistributorBotResponseMessage.DISTRIBUTION_RESULT_IN_A_FILE.getMessageText(),
                CargoDistributorBotResponseMessage.RETURNING_TO_START.getMessageText(),
                CargoDistributorBotResponseMessage.DISTRIBUTION_RESULT.getMessageText()
        );
    }

    @Test
    void processCommandAndGetResponseMessages_vanLimitDoesNotFit() {
        CargoDistributorBotService botService = new CargoDistributorBotService(10);

        long chatId = 123L;

        List<CargoItem> cargoItems = new ArrayList<>();
        cargoItems.add(new CargoItem(1, 1, 1));
        cargoItems.add(new CargoItem(2, 1, 2));
        cargoItems.add(new CargoItem(3, 1, 3));
        CargoItemList cargoItemList = new CargoItemList(cargoItems);

        botService.putCargoItemListToCache(String.valueOf(chatId), cargoItemList);
        botService.putVanLimitToCache(String.valueOf(chatId), 1);

        CommandHandlerService handlerService = new Step4DistributionFromFileCommandHandlerService(
                botService,
                new CargoConverterService(),
                new FileService(true)
        );

        Chat chat = new Chat(chatId, "private");

        Message message = new Message();
        message.setText(DistributionAlgorithmName.ONE_VAN_ONE_ITEM.getTitle());
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
                CargoDistributorBotResponseMessage.FAILED_TO_DISTRIBUTE_UNDER_VAN_LIMIT.getMessageText(),
                CargoDistributorBotResponseMessage.RETURNING_TO_START.getMessageText()
        );
    }

    @Test
    void processCommandAndGetResponseMessages_invalidAlgorithm() {
        long chatId = 123L;

        CommandHandlerService handlerService = new Step4DistributionFromFileCommandHandlerService(
                new CargoDistributorBotService(10),
                new CargoConverterService(),
                new FileService(true)
        );

        Chat chat = new Chat(chatId, "private");

        Message message = new Message();
        message.setText("you can do whatever you want");
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
                CargoDistributorBotResponseMessage.CANT_RESOLVE_PICKED_ALGORITHM_NAME.getMessageText(),
                CargoDistributorBotResponseMessage.PICK_ALGORITHM.getMessageText()
        );
    }
}