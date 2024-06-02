package ru.liga.cargodistributor.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;
import ru.liga.cargodistributor.cargo.entity.CargoVanTypeInfo;

public class CargoDistributorBotChatData {
    private CargoItemList cargoItemList;
    private int vanLimit;
    private SendMessage lastMessage;
    private String cargoItemTypeName;
    private String cargoItemTypeLegend;
    private CargoItemTypeInfo cargoItemTypeInfoToUpdate;
    private CargoVanTypeInfo cargoVanTypeInfo;

    //todo: мб стоит добавить сюда билдер
    public CargoDistributorBotChatData(
            CargoItemList cargoItemList,
            SendMessage lastMessage,
            int vanLimit,
            String cargoItemTypeName,
            String cargoItemTypeLegend,
            CargoItemTypeInfo cargoItemTypeInfoToUpdate,
            CargoVanTypeInfo cargoVanTypeInfo
    ) {
        this.cargoItemList = cargoItemList;
        this.vanLimit = vanLimit;
        this.lastMessage = lastMessage;
        this.cargoItemTypeName = cargoItemTypeName;
        this.cargoItemTypeLegend = cargoItemTypeLegend;
        this.cargoItemTypeInfoToUpdate = cargoItemTypeInfoToUpdate;
        this.cargoVanTypeInfo = cargoVanTypeInfo;
    }

    public CargoDistributorBotChatData(CargoItemList cargoItemList) {
        this(cargoItemList, null, 0, null, null, null, null);
    }

    public CargoDistributorBotChatData(SendMessage lastMessage) {
        this(null, lastMessage, 0, null, null, null, null);
    }

    public CargoDistributorBotChatData(int vanLimit) {
        this(null, null, vanLimit, null, null, null, null);
    }

    public CargoDistributorBotChatData(String cargoItemTypeName) {
        this(null, null, 0, cargoItemTypeName, null, null, null);
    }

    public CargoDistributorBotChatData(CargoItemTypeInfo cargoItemTypeInfoToUpdate) {
        this(null, null, 0, null, null, cargoItemTypeInfoToUpdate, null);
    }

    public CargoDistributorBotChatData(CargoVanTypeInfo cargoVanTypeInfo) {
        this(null, null, 0, null, null, null, cargoVanTypeInfo);
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

    public CargoItemTypeInfo getCargoItemTypeInfoToUpdate() {
        return cargoItemTypeInfoToUpdate;
    }

    public void setCargoItemTypeInfoToUpdate(CargoItemTypeInfo cargoItemTypeInfoToUpdate) {
        this.cargoItemTypeInfoToUpdate = cargoItemTypeInfoToUpdate;
    }

    public CargoVanTypeInfo getCargoVanTypeInfo() {
        return cargoVanTypeInfo;
    }

    public void setCargoVanTypeInfo(CargoVanTypeInfo cargoVanTypeInfo) {
        this.cargoVanTypeInfo = cargoVanTypeInfo;
    }
}
