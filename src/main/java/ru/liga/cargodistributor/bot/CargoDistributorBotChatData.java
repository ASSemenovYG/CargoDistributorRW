package ru.liga.cargodistributor.bot;

import lombok.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.liga.cargodistributor.algorithm.CargoDistributionParameters;
import ru.liga.cargodistributor.cargo.CargoItemList;
import ru.liga.cargodistributor.cargo.entity.CargoItemTypeInfo;
import ru.liga.cargodistributor.cargo.entity.CargoVanTypeInfo;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CargoDistributorBotChatData {
    private CargoItemList cargoItemList;
    private int vanLimit;
    private SendMessage lastMessage;
    private String cargoItemTypeName;
    private String cargoItemTypeLegend;
    private CargoItemTypeInfo cargoItemTypeInfoToUpdate;
    private CargoVanTypeInfo cargoVanTypeInfo;
    private CargoDistributionParameters cargoDistributionParameters;
}
