package ru.liga.cargodistributor.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.liga.cargodistributor.cargo.CargoItemList;

public class CargoDistributorBotChatData {
    private CargoItemList cargoItemList;
    private int vanLimit;
    private SendMessage lastMessage;
    private String cargoItemTypeName;
    private String cargoItemTypeLegend;

    public CargoDistributorBotChatData(CargoItemList cargoItemList, SendMessage lastMessage, int vanLimit, String cargoItemTypeName, String cargoItemTypeLegend) {
        this.cargoItemList = cargoItemList;
        this.vanLimit = vanLimit;
        this.lastMessage = lastMessage;
        this.cargoItemTypeName = cargoItemTypeName;
        this.cargoItemTypeLegend = cargoItemTypeLegend;
    }

    public CargoDistributorBotChatData(CargoItemList cargoItemList) {
        this(cargoItemList, null, 0, null, null);
    }

    public CargoDistributorBotChatData(SendMessage lastMessage) {
        this(null, lastMessage, 0, null, null);
    }

    public CargoDistributorBotChatData(int vanLimit) {
        this(null, null, vanLimit, null, null);
    }

    public CargoDistributorBotChatData(String cargoItemTypeName) {
        this(null, null, 0, cargoItemTypeName, null);
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

    public String getCargoItemTypeName() {
        return cargoItemTypeName;
    }

    public void setCargoItemTypeName(String cargoItemTypeName) {
        this.cargoItemTypeName = cargoItemTypeName;
    }

    public String getCargoItemTypeLegend() {
        return cargoItemTypeLegend;
    }

    public void setCargoItemTypeLegend(String cargoItemTypeLegend) {
        this.cargoItemTypeLegend = cargoItemTypeLegend;
    }
}
