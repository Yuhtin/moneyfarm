package com.yuhtin.quotes.moneyfarm.command;

import com.yuhtin.quotes.moneyfarm.MoneyFarm;
import com.yuhtin.quotes.moneyfarm.manager.StorageManager;
import com.yuhtin.quotes.moneyfarm.model.StorageFarm;
import com.yuhtin.quotes.moneyfarm.util.ColorUtil;
import com.yuhtin.quotes.moneyfarm.util.NumberUtils;
import com.yuhtin.quotes.moneyfarm.view.StorageView;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class MoneyFarmCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        StorageManager storageManager = MoneyFarm.getInstance().getStorageManager();
        if (arguments.length > 3) {
            if (arguments[0].equalsIgnoreCase("give")) {
                Player player = Bukkit.getPlayerExact(arguments[1]);
                if (player == null) {
                    sender.sendMessage(ColorUtil.colored("&cJogador inexistente."));
                    return false;
                }

                String farmType = arguments[2];
                double quantity = NumberUtils.parse(arguments[3]);
                if (quantity <= 0) {
                    sender.sendMessage(ColorUtil.colored("&cQuantidade inválida."));
                    return false;
                }

                StorageFarm storageFarm = storageManager.getByPlayer(player);
                if (storageManager.increaseStack(player, storageFarm, farmType, quantity)) {
                    sender.sendMessage(ColorUtil.colored("&aAdicionado &f" + NumberUtils.format(quantity) + "x &a" + farmType + " &aao jogador " + player.getName() + "."));
                    return true;
                } else {
                    return false;
                }
            }
        }

        if (sender instanceof ConsoleCommandSender) return false;

        Player player = (Player) sender;

        StorageFarm storageFarm = storageManager.getByPlayer(player);
        StorageView.singleton().openInventory(player, viewer -> viewer.getPropertyMap().set("storageFarm", storageFarm));
        return true;
    }
}
