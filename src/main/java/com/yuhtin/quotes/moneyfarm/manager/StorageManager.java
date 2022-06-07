package com.yuhtin.quotes.moneyfarm.manager;

import com.yuhtin.quotes.moneyfarm.cache.StorageCache;
import com.yuhtin.quotes.moneyfarm.model.FarmItem;
import com.yuhtin.quotes.moneyfarm.model.StorageFarm;
import com.yuhtin.quotes.moneyfarm.model.StorageFarmItem;
import com.yuhtin.quotes.moneyfarm.sql.StorageDAO;
import com.yuhtin.quotes.moneyfarm.sql.connection.SQLConnection;
import com.yuhtin.quotes.moneyfarm.sql.connection.mysql.MySQLConnection;
import com.yuhtin.quotes.moneyfarm.sql.connection.sqlite.SQLiteConnection;
import com.yuhtin.quotes.moneyfarm.util.ColorUtil;
import lombok.Getter;
import lombok.val;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class StorageManager {

    private final StorageCache storageCache = new StorageCache();
    private StorageDAO storageDAO;

    public void init(JavaPlugin plugin) {
        ConfigurationSection connectionSection = plugin.getConfig().getConfigurationSection("connection");
        SQLConnection sql = new MySQLConnection();
        if (!sql.configure(connectionSection.getConfigurationSection("mysql"))) {
            sql = new SQLiteConnection();
            sql.configure(connectionSection.getConfigurationSection("sqlite"));
        }

        storageDAO = new StorageDAO();
        storageDAO.setSqlConnection(sql);
        storageDAO.createTable();

        storageCache.init();
    }

    public StorageFarm getByPlayer(Player player) {
        StorageFarm cached = storageCache.getCache().getOrDefault(player.getName(), null);
        if (cached != null) return cached;

        StorageFarm farm = storageDAO.find(player.getName());
        if (farm == null) {
            farm = new StorageFarm();
            storageDAO.save(player.getName(), farm);
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
            storageDAO.save(entry.getKey(), entry.getValue());
        }
    }
}
