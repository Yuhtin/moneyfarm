package com.yuhtin.quotes.moneyfarm.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class StorageFarmItem extends FarmItem {

    public StorageFarmItem(FarmItem farmItem) {
        super(farmItem.getItem(), farmItem.getInterval(), farmItem.getCoinsPerItem());

        this.quantity = 1;
        this.lastGenerationTime = 0;
    }

    private double coins;
    private double quantity;
    private long lastGenerationTime;

}
