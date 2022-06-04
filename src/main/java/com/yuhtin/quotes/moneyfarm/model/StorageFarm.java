package com.yuhtin.quotes.moneyfarm.model;

import com.yuhtin.quotes.moneyfarm.MoneyFarm;
import lombok.Data;
import lombok.val;
import lombok.var;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Data
public class StorageFarm {

    private final List<StorageFarmItem> farmItems = new ArrayList<>();

    public boolean increaseStack(Player player, String identifier, int amount) {
        val stackLimit = MoneyFarm.getInstance().getStorageCache().getStackLimit();
        var limit = stackLimit.get("default");
        for (val entry : stackLimit.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("default")) continue;
            if (player.hasPermission(entry.getKey())) {
                limit = entry.getValue();
                break;
            }
        }

        for (StorageFarmItem storageFarmItem : farmItems) {
            if (storageFarmItem.getIdentifier().equals(identifier)) {
                double quantity = storageFarmItem.getQuantity() + amount;
                if (quantity > limit) {
                    player.sendMessage("§cVocê não pode colocar mais do que §f" + limit + "x §c" + storageFarmItem.getIdentifier());
                    return false;
                }


                storageFarmItem.setQuantity(quantity);
                return true;
            }
        }

        return false;
    }

}
