package com.yuhtin.quotes.moneyfarm.task;

import com.yuhtin.quotes.moneyfarm.MoneyFarm;
import com.yuhtin.quotes.moneyfarm.model.StorageFarm;
import lombok.val;
import lombok.var;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;

public class StorageUpdateTask implements TerminableModule {

    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {
        Schedulers.async().runRepeating(() -> {
            val instance = MoneyFarm.getInstance();
            val cache = instance.getStorageCache();
            for (val player : Bukkit.getOnlinePlayers()) {
                var storage = cache.getCache().getOrDefault(player.getName(), null);
                if (storage == null) {
                    StorageFarm storageFarm = instance.getStorageRepository().selectOne(player.getName());
                    if (storageFarm == null) {
                        storage = new StorageFarm();
                        instance.getStorageRepository().saveOne(player.getName(), storage);
                    } else {
                        storage = storageFarm;
                    }
                }

                for (val item : storage.getFarmItems()) {

                    val intervalInMillis = item.getInterval() * 1000L;
                    val lastGenerationTime = item.getLastGenerationTime();
                    if (System.currentTimeMillis() < lastGenerationTime) continue;

                    val executes = (int) ((System.currentTimeMillis() - lastGenerationTime) / intervalInMillis);
                    if (executes < 1) continue;

                    item.setLastGenerationTime(System.currentTimeMillis());

                    var quantityGenerated = (item.getQuantity() * item.getCoinsPerItem()) * executes;
                    for (val entry : cache.getGenerationMultipliers().entrySet()) {
                        if (player.hasPermission(entry.getKey())) {
                            quantityGenerated *= entry.getValue();
                            break;
                        }
                    }

                    item.setCoins(item.getCoins() + quantityGenerated);
                }
            }

        }, 0L, 50L);
    }
}
