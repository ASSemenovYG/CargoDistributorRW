package ru.liga.cargodistributor.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.liga.cargodistributor.cargo.CargoItemList;

public class CargoDistributorBotChatData {
    private CargoItemList cargoItemList;
    private int vanLimit;
    private SendMessage lastMessage;

    public CargoDistributorBotChatData(CargoItemList cargoItemList, SendMessage lastMessage, int vanLimit) {
        this.cargoItemList = cargoItemList;
        this.vanLimit = vanLimit;
        this.lastMessage = lastMessage;
    }

    public CargoDistributorBotChatData(CargoItemList cargoItemList) {
        this(cargoItemList, null, 0);
    }

    public CargoDistributorBotChatData(SendMessage lastMessage) {
        this(null, lastMessage, 0);
    }

    public CargoDistributorBotChatData(int vanLimit) {
        this(null, null, vanLimit);
    }

    public CargoItemList getCargoItemList() {
        return cargoItemList;
    }

    public void setCargoItemList(CargoItemList cargoItemList) {
        this.cargoItemList = cargoItemList;
    }

    public int getVanLimit() {
        return vanLimit;
    }

    public void setVanLimit(int vanLimit) {
        this.vanLimit = vanLimit;
    }

    public SendMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(SendMessage lastMessage) {
        this.lastMessage = lastMessage;
    }
}
