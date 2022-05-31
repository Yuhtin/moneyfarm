package com.yuhtin.quotes.moneyfarm.util;

import com.yuhtin.quotes.moneyfarm.MoneyFarm;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class EconomyHook {

    private Economy economy;

    public void init() {
        RegisteredServiceProvider<Economy> registration = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (registration == null) {
            MoneyFarm.getInstance().getLogger().severe("Não foi encontrado nenhum plugin de economia no servidor!");
        } else {
            economy = registration.getProvider();
        }
    }

    public double getBalance(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    public void depositCoins(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }

    public EconomyResponse withdrawCoins(OfflinePlayer player, double amount) {
        if (has(player, amount)) return economy.withdrawPlayer(player, amount);
        else return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.FAILURE, "");
    }

    public boolean has(OfflinePlayer player, double amount) {
        // avoid unimplemented economy methods
        return economy.has(player, amount) || getBalance(player) >= amount;
    }

}