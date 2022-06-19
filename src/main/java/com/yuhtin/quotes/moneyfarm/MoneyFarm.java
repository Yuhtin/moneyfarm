package com.yuhtin.quotes.moneyfarm;

import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.yuhtin.quotes.moneyfarm.command.MoneyFarmCommand;
import com.yuhtin.quotes.moneyfarm.manager.StorageManager;
import com.yuhtin.quotes.moneyfarm.task.StorageUpdateTask;
import com.yuhtin.quotes.moneyfarm.util.EconomyHook;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class MoneyFarm extends JavaPlugin {

    private final StorageManager storageManager = new StorageManager();
    private final EconomyHook economyHook = new EconomyHook();

    @Override
    public void onEnable() {
        InventoryManager.enable(this);

        saveDefaultConfig();

        economyHook.init();
        storageManager.init(this);

        StorageUpdateTask storageUpdateTask = new StorageUpdateTask();
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, storageUpdateTask, 0L, 50L);

        getCommand("farm").setExecutor(new MoneyFarmCommand());
        getLogger().info("MoneyFarm has been enabled!");
    }

    @Override
    public void onDisable() {
        storageManager.save();
        getLogger().info("MoneyFarm has been disabled!");
    }

    public static MoneyFarm getInstance() {
        return JavaPlugin.getPlugin(MoneyFarm.class);
    }

}
