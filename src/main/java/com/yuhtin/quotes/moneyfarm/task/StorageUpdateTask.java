package com.yuhtin.quotes.moneyfarm.task;

import com.yuhtin.quotes.moneyfarm.MoneyFarm;
import com.yuhtin.quotes.moneyfarm.model.StorageFarm;
import lombok.val;
import lombok.var;
import org.bukkit.Bukkit;

public class StorageUpdateTask implements Runnable {

    @Override
    public void run() {
        val instance = MoneyFarm.getInstance();
        val cache = instance.getStorageManager();
        for (val player : Bukkit.getOnlinePlayers()) {
            StorageFarm storage = cache.getByPlayer(player);
            for (val item : storage.getFarmItems()) {

                val intervalInMillis = item.getInterval() * 1000L;
                val lastGenerationTime = item.getLastGenerationTime();
                if (System.currentTimeMillis() < lastGenerationTime) continue;

                val executes = (int) ((System.currentTimeMillis() - lastGenerationTime) / intervalInMillis);
                if (executes < 1) continue;

                item.setLastGenerationTime(System.currentTimeMillis());

                var quantityGenerated = (item.getQuantity() * item.getCoinsPerItem()) * executes;
                for (val entry : cache.getStorageCache().getGenerationMultipliers().entrySet()) {
                    if (player.hasPermission("moneyfarm." + entry.getKey())) {
                        quantityGenerated *= entry.getValue();
                        break;
                    }
                }

                item.setCoins(item.getCoins() + quantityGenerated);
            }
        }
    }
}
