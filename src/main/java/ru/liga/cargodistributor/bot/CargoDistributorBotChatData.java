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

    private CargoDistributorBotChatData(Builder builder) {
        this.cargoItemList = builder.cargoItemList;
        this.vanLimit = builder.vanLimit;
        this.lastMessage = builder.lastMessage;
        this.cargoItemTypeName = builder.cargoItemTypeName;
        this.cargoItemTypeLegend = builder.cargoItemTypeLegend;
        this.cargoItemTypeInfoToUpdate = builder.cargoItemTypeInfoToUpdate;
        this.cargoVanTypeInfo = builder.cargoVanTypeInfo;
    }

    public static class Builder {
        private CargoItemList cargoItemList;
        private int vanLimit;
        private SendMessage lastMessage;
        private String cargoItemTypeName;
        private String cargoItemTypeLegend;
        private CargoItemTypeInfo cargoItemTypeInfoToUpdate;
        private CargoVanTypeInfo cargoVanTypeInfo;

        public Builder() {
        }

        public Builder setCargoItemList(CargoItemList cargoItemList) {
            this.cargoItemList = cargoItemList;
            return this;
        }

        public Builder setVanLimit(int vanLimit) {
            this.vanLimit = vanLimit;
            return this;
        }

        public Builder setLastMessage(SendMessage lastMessage) {
            this.lastMessage = lastMessage;
            return this;
        }

        public Builder setCargoItemTypeName(String cargoItemTypeName) {
            this.cargoItemTypeName = cargoItemTypeName;
            return this;
        }

        public Builder setCargoItemTypeLegend(String cargoItemTypeLegend) {
            this.cargoItemTypeLegend = cargoItemTypeLegend;
            return this;
        }

        public Builder setCargoItemTypeInfoToUpdate(CargoItemTypeInfo cargoItemTypeInfoToUpdate) {
            this.cargoItemTypeInfoToUpdate = cargoItemTypeInfoToUpdate;
            return this;
        }

        public Builder setCargoVanTypeInfo(CargoVanTypeInfo cargoVanTypeInfo) {
            this.cargoVanTypeInfo = cargoVanTypeInfo;
            return this;
        }

        public CargoDistributorBotChatData build() {
            return new CargoDistributorBotChatData(this);
        }
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
