package ru.liga.cargodistributor.bot.services;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.liga.cargodistributor.bot.CargoDistributorBot;
import ru.liga.cargodistributor.bot.enums.CargoDistributorBotKeyboard;
import ru.liga.cargodistributor.bot.exceptions.GetFileFromUpdateException;
import ru.liga.cargodistributor.cargo.CargoItem;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.util.services.FileService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
class CargoDistributorBotServiceTest {
    @Autowired
    private CargoDistributorBot cargoDistributorBot;

    private final CargoDistributorBotService botService = new CargoDistributorBotService(10);

    @Test
    void buildTextMessageWithoutKeyboard() {
        SendMessage message = botService.buildTextMessageWithoutKeyboard(123L, "hello");

        assertThat(message.getText()).isEqualTo("hello");
        assertThat(message.getChatId()).isEqualTo("123");
        assertThat(message.getReplyMarkup().getClass()).isEqualTo(ReplyKeyboardRemove.class);
    }

    @Test
    void buildTextMessageWithKeyboard_ValidKeyboard() {
        SendMessage message = botService.buildTextMessageWithKeyboard(
                123L,
                "message with keyboard",
                CargoDistributorBotKeyboard.PICK_ALGORITHM
        );
        assertThat(message.getText()).isEqualTo("message with keyboard");
        assertThat(message.getChatId()).isEqualTo("123");
        assertThat(message.getReplyMarkup().getClass()).isEqualTo(ReplyKeyboardMarkup.class);
    }

    @Test
    void buildTextMessageWithKeyboard_NullKeyboard() {
        assertThrows(NullPointerException.class, () ->
                botService.buildTextMessageWithKeyboard(
                        123L,
                        "message with null keyboard",
                        null
                ));
    }

    @Test
    void buildDocumentMessage() {
        SendDocument sendDocument = botService.buildDocumentMessage(123L, "src/test/resources/test_hello_world.json", "test_hello_world.json");

        assertThat(sendDocument.getChatId()).isEqualTo("123");
        assertThat(sendDocument.getDocument().getMediaName()).isEqualTo("test_hello_world.json");
        assertThat(sendDocument.getFile().getNewMediaFile().getPath()).isEqualTo("src\\test\\resources\\test_hello_world.json");
    }

    @Test
    void buildStickerMessage() {
        String stickerId = "CAACAgIAAxkBAAEL9b5mJFsmbk4g6TvaIYybFKi2wDtuNQACSSwAAnodcUrY0E6TPhUOwjQE";
        SendSticker sendSticker = botService.buildStickerMessage(123L, stickerId);

        assertThat(sendSticker.getChatId()).isEqualTo("123");
        assertThat(sendSticker.getSticker().getAttachName()).isEqualTo(stickerId);
    }

    @Test
    void buildMessageWithVideo() {
        String videoId = "BAACAgIAAxkBAAPnZiRfZOTOCEhcoD205Iz6fDSiTbMAAqZTAALkTSBJd9KpquJL27A0BA";
        SendVideoNote sendVideoNote = botService.buildMessageWithVideo(123L, videoId);

        assertThat(sendVideoNote.getChatId()).isEqualTo("123");
        assertThat(sendVideoNote.getVideoNote().getAttachName()).isEqualTo(videoId);
    }

    @Test
    void getLastSendMessageFromCache() {
        SendMessage messageFromCache = botService.getLastSendMessageFromCache("123");
        assertThat(messageFromCache).isNull();

        String lastMessageText = "i just cant get you out of my head";
        SendMessage lastMessage = botService.buildTextMessageWithoutKeyboard(123L, lastMessageText);
        botService.putLastMessageToCache("123", lastMessage);

        messageFromCache = botService.getLastSendMessageFromCache("123");
        assertThat(messageFromCache.getText()).isEqualTo(lastMessageText);

        SendMessage messageNotFoundInCache = botService.getLastSendMessageFromCache("322");
        assertThat(messageNotFoundInCache).isNull();
    }

    @Test
    void getCargoItemListFromCache() {
        CargoItemList cargoItemListFromCache = botService.getCargoItemListFromCache("42");
        assertThat(cargoItemListFromCache).isNull();

        List<CargoItem> cargoItems = new ArrayList<>();
        cargoItems.add(new CargoItem(1, 1, 1));
        cargoItems.add(new CargoItem(2, 1, 2));
        cargoItems.add(new CargoItem(3, 1, 3));
        CargoItemList cargoItemList = new CargoItemList(cargoItems);

        botService.putCargoItemListToCache("42", cargoItemList);

        cargoItemListFromCache = botService.getCargoItemListFromCache("42");
        assertThat(cargoItemListFromCache.getCargo()).isEqualTo(cargoItemList.getCargo());

        CargoItemList cargoItemListNotFoundInCache = botService.getCargoItemListFromCache("322");
        assertThat(cargoItemListNotFoundInCache).isNull();
    }

    @Test
    void getVanLimitFromCache() {
        int vanLimitFromCache = botService.getVanLimitFromCache("442");
        assertThat(vanLimitFromCache).isEqualTo(0);

        botService.putVanLimitToCache("442", 42);
        vanLimitFromCache = botService.getVanLimitFromCache("442");
        assertThat(vanLimitFromCache).isEqualTo(42);

        int vanLimitNotFoundInCache = botService.getVanLimitFromCache("322");
        assertThat(vanLimitNotFoundInCache).isEqualTo(0);
    }

    @Test
    void getFileFromUpdate_ValidFile() {
        Chat chat = new Chat(1337L, "private");

        Document document = new Document(
                "BQACAgIAAxkBAAICImZBAAFaKNCP6NvuteJuYOQmUwX3EAAC0UsAAoCYCUqv4yRB0jnEYTUE",
                "AgAD0UsAAoCYCUo",
                null,
                "test_valid_cargo_file_3.pkg",
                "application/x-xar",
                156L
        );

        Message message = new Message();
        message.setChat(chat);
        message.setDocument(document);

        Update update = new Update();
        update.setMessage(message);

        File fileFromUpdate = botService.getFileFromUpdate(update, new OkHttpTelegramClient(cargoDistributorBot.getBotToken()));
        assertThat(fileFromUpdate.isFile()).isTrue();

        FileService fileService = new FileService(true);
        String fileFromUpdateContent = fileService.readFromFile(fileFromUpdate);
        String testFileContent = fileService.readFromFileByPath("src/test/resources/test_valid_cargo_file_3.pkg");

        assertThat(fileFromUpdateContent).isEqualTo(testFileContent);
    }

    @Test
    void getFileFromUpdate_NullFile() {
        Chat chat = new Chat(1337L, "private");

        Document document = new Document();

        Message message = new Message();
        message.setChat(chat);
        message.setDocument(document);

        Update update = new Update();
        update.setMessage(message);

        assertThrows(NullPointerException.class, () ->
                botService.getFileFromUpdate(
                        update,
                        new OkHttpTelegramClient(cargoDistributorBot.getBotToken())
                )
        );
    }

    @Test
    void getFileFromUpdate_TelegramClient404() {
        Chat chat = new Chat(1337L, "private");

        Document document = new Document(
                "BQACAgIAAxkBAAICImZBAAFaKNCP6NvuteJuYOQmUwX3EAAC0UsAAoCYCUqv4yRB0jnEYTUE",
                "AgAD0UsAAoCYCUo",
                null,
                "test_valid_cargo_file_3.pkg",
                "application/x-xar",
                156L
        );

        Message message = new Message();
        message.setChat(chat);
        message.setDocument(document);

        Update update = new Update();
        update.setMessage(message);

        assertThrows(GetFileFromUpdateException.class, () ->
                botService.getFileFromUpdate(
                        update,
                        new OkHttpTelegramClient("token")
                )
        );
    }
}