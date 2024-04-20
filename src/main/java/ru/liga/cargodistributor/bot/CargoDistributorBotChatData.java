package ru.liga.cargodistributor.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.CargoVanList;

public class CargoDistributorBotChatData {
    private CargoItemList cargoItemList;
    private CargoVanList cargoVanList;
    private int vanLimit;
    private SendMessage lastMessage;

    public CargoDistributorBotChatData(CargoItemList cargoItemList, CargoVanList cargoVanList, SendMessage lastMessage, int vanLimit) {
        this.cargoItemList = cargoItemList;
        this.cargoVanList = cargoVanList;
        this.vanLimit = vanLimit;
        this.lastMessage = lastMessage;
    }

    public CargoDistributorBotChatData(CargoItemList cargoItemList) {
        this(cargoItemList, null, null, 0);
    }

    public CargoDistributorBotChatData(CargoVanList cargoVanList) {
        this(null, cargoVanList, null, 0);
    }

    public CargoDistributorBotChatData(SendMessage lastMessage) {
        this(null, null, lastMessage, 0);
    }

    public CargoDistributorBotChatData(int vanLimit) {
        this(null, null, null, vanLimit);
    }

    public CargoItemList getCargoItemList() {
        return cargoItemList;
    }

    public void setCargoItemList(CargoItemList cargoItemList) {
        this.cargoItemList = cargoItemList;
    }

    public CargoVanList getCargoVanList() {
        return cargoVanList;
    }

    public void setCargoVanList(CargoVanList cargoVanList) {
        this.cargoVanList = cargoVanList;
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
