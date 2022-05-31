package com.yuhtin.quotes.moneyfarm;

import com.yuhtin.quotes.moneyfarm.cache.StorageCache;
import com.yuhtin.quotes.moneyfarm.command.MoneyFarmCommand;
import com.yuhtin.quotes.moneyfarm.util.EconomyHook;
import lombok.Getter;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class MoneyFarm extends ExtendedJavaPlugin {

    private final StorageCache storageCache = new StorageCache();
    private final EconomyHook economyHook = new EconomyHook();

    @Override
    public void enable() {
        economyHook.init();
        getCommand("moneyfarm").setExecutor(new MoneyFarmCommand());

        getLogger().info("MoneyFarm has been enabled!");
    }

    @Override
    protected void disable() {

    }

    public static MoneyFarm getInstance() {
        return JavaPlugin.getPlugin(MoneyFarm.class);
    }

}
