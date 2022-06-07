package com.yuhtin.quotes.moneyfarm.manager;

import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.yuhtin.quotes.moneyfarm.cache.StorageCache;
import com.yuhtin.quotes.moneyfarm.dao.SQLProvider;
import com.yuhtin.quotes.moneyfarm.dao.repository.StorageRepository;
import com.yuhtin.quotes.moneyfarm.model.FarmItem;
import com.yuhtin.quotes.moneyfarm.model.StorageFarm;
import com.yuhtin.quotes.moneyfarm.model.StorageFarmItem;
import com.yuhtin.quotes.moneyfarm.util.ColorUtil;
import lombok.Getter;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class StorageManager {

    private final StorageCache storageCache = new StorageCache();
    private StorageRepository storageRepository;

    public void init(JavaPlugin plugin) {
        SQLConnector setup = SQLProvider.of(plugin).setup(null);

        storageRepository = new StorageRepository(new SQLExecutor(setup));
        storageRepository.createTable();

        storageCache.init();
    }

    public StorageFarm getByPlayer(Player player) {
        StorageFarm cached = storageCache.getCache().getOrDefault(player.getName(), null);
        if (cached != null) return cached;

        StorageFarm farm = storageRepository.selectOne(player.getName());
        if (farm == null) {
            farm = new StorageFarm();
            storageRepository.saveOne(player.getName(), farm);
        }

        storageCache.getCache().put(player.getName(), farm);
        return farm;
    }

    public boolean increaseStack(Player player, StorageFarm storageFarm, String farmIdentifier, double amount) {
        double limit = storageCache.getStackLimit().getOrDefault("default", 10d);
        for (val entry : storageCache.getStackLimit().entrySet()) {
            if (entry.getKey().equalsIgnoreCase("default")) continue;
            if (player.hasPermission("moneyfarm." + entry.getKey())) {
                limit = entry.getValue();
                break;
            }
        }

        for (StorageFarmItem storageFarmItem : storageFarm.getFarmItems()) {
            if (storageFarmItem.getIdentifier().equals(farmIdentifier)) {
                double quantity = storageFarmItem.getQuantity() + amount;
                if (quantity > limit) {
                    player.sendMessage(ColorUtil.colored("&cVocê não pode colocar mais do que &f" + limit + "x &c" + storageFarmItem.getIdentifier() + "."));
                    return false;
                }

                storageFarmItem.setQuantity(quantity);
                return true;
            }
        }

        if (amount > limit) {
            player.sendMessage(ColorUtil.colored("&cVocê não pode colocar mais do que &f" + limit + "x &c" + farmIdentifier + "."));
            return false;
        }

        FarmItem farmItem = storageCache.getFarmItems().getOrDefault(farmIdentifier, null);
        if (farmItem == null) {
            player.sendMessage(ColorUtil.colored("&cItem inválido."));
            return false;
        }

        StorageFarmItem storageFarmItem = new StorageFarmItem(farmItem);
        storageFarmItem.setQuantity(amount);

        storageFarm.getFarmItems().add(storageFarmItem);
        return true;
    }

    public void save() {
        for (val entry : storageCache.getCache().entrySet()) {
            storageRepository.saveOne(entry.getKey(), entry.getValue());
        }
    }
}
