package com.yuhtin.quotes.moneyfarm.model;

import com.yuhtin.quotes.moneyfarm.MoneyFarm;
import com.yuhtin.quotes.moneyfarm.cache.StorageCache;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class StorageFarmItem extends FarmItem {

    private double coins;
    private double quantity;
    private long lastGenerationTime;

    public StorageFarmItem(FarmItem farmItem) {
        super(farmItem.getIdentifier(), farmItem.getItem(), farmItem.getInterval(), farmItem.getCoinsPerItem());

        this.quantity = 1;
        this.lastGenerationTime = 0;
    }

    @Nullable
    public static StorageFarmItem fromString(String data) {
        String[] split = data.split("-");
        String identifier = split[0];
        double coins = Double.parseDouble(split[1]);
        double quantity = Double.parseDouble(split[2]);
        long lastGenerationTime = Long.parseLong(split[3]);

        StorageCache storageCache = MoneyFarm.getInstance().getStorageManager().getStorageCache();
        FarmItem farmItem = storageCache.getFarmItems().getOrDefault(identifier, null);
        if (farmItem == null) return null;

        StorageFarmItem storageFarmItem = new StorageFarmItem(farmItem);
        storageFarmItem.setCoins(coins);
        storageFarmItem.setQuantity(quantity);
        storageFarmItem.setLastGenerationTime(lastGenerationTime);

        return storageFarmItem;
    }

    public String toString() {
        return this.getIdentifier() + "-" + coins + "-" + quantity + "-" + lastGenerationTime;
    }

}
