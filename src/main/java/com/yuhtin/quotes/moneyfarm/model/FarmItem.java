package com.yuhtin.quotes.moneyfarm.model;

import lombok.Builder;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Builder
@Data
public class FarmItem {

    private final ItemStack item;
    private final int interval;
    private final double coinsPerItem;

}
