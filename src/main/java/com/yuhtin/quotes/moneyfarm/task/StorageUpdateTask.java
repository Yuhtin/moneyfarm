package com.yuhtin.quotes.moneyfarm.task;

import com.yuhtin.quotes.moneyfarm.MoneyFarm;
import com.yuhtin.quotes.moneyfarm.model.StorageFarm;
import com.yuhtin.quotes.moneyfarm.model.StorageFarmItem;
import me.lucko.helper.Schedulers;
import me.lucko.helper.bucket.Bucket;
import me.lucko.helper.bucket.BucketPartition;
import me.lucko.helper.bucket.factory.BucketFactory;
import me.lucko.helper.bucket.partitioning.PartitioningStrategies;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;

import javax.annotation.Nonnull;

public class StorageUpdateTask implements TerminableModule {

    private final Bucket<StorageFarmItem> bucket = BucketFactory.newHashSetBucket(20, PartitioningStrategies.lowestSize());

    @Override
    public void setup(@Nonnull TerminableConsumer consumer) {
        Schedulers.async().runRepeating(() -> {
            bucket.clear();

            for (StorageFarm value : MoneyFarm.getInstance().getStorageCache().getCache().values()) {
                bucket.addAll(value.getFarmItems());
            }
        }, 0L, 50L);

        Schedulers.async().runRepeating(() -> {
            BucketPartition<StorageFarmItem> part = bucket.asCycle().next();
            for (StorageFarmItem item : part) {
                long intervalInMillis = item.getInterval() * 1000L;
                long lastGenerationTime = item.getLastGenerationTime();
                if (System.currentTimeMillis() < lastGenerationTime) continue;

                int executes = (int) ((System.currentTimeMillis() - lastGenerationTime) / intervalInMillis);
                if (executes < 1) continue;

                item.setLastGenerationTime(System.currentTimeMillis());

                double quantityGenerated = (item.getQuantity() * item.getCoinsPerItem()) * executes;
                item.setCoins(item.getCoins() + quantityGenerated);
            }
        }, 1L, 1L);
    }
}
