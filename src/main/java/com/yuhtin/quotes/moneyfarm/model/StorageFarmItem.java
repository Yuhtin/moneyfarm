package com.yuhtin.quotes.moneyfarm.model;

import com.yuhtin.quotes.moneyfarm.MoneyFarm;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class StorageFarmItem extends FarmItem {

    public StorageFarmItem(FarmItem farmItem) {
        super(farmItem.getIdentifier(), farmItem.getItem(), farmItem.getInterval(), farmItem.getCoinsPerItem());

        this.quantity = 1;
        this.lastGenerationTime = 0;
    }

    private double coins;
    private double quantity;
    private long lastGenerationTime;

    public String toString() {
        return this.getIdentifier() + "-" + coins + "-" + quantity + "-" + lastGenerationTime;
    }

    public static StorageFarmItem fromString(String data) {
        String[] split = data.split("-");
        String identifier = split[0];
        double coins = Double.parseDouble(split[1]);
        double quantity = Double.parseDouble(split[2]);
        long lastGenerationTime = Long.parseLong(split[3]);

        StorageFarmItem storageFarmItem = new StorageFarmItem(MoneyFarm.getInstance().getStorageCache().getFarmItem().get(identifier));
        storageFarmItem.setCoins(coins);
        storageFarmItem.setQuantity(quantity);
        storageFarmItem.setLastGenerationTime(lastGenerationTime);

        return storageFarmItem;
    }

}
