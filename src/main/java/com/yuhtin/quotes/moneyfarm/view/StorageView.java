package com.yuhtin.quotes.moneyfarm.view;

import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.border.Border;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import com.yuhtin.quotes.moneyfarm.MoneyFarm;
import com.yuhtin.quotes.moneyfarm.model.StorageFarm;
import com.yuhtin.quotes.moneyfarm.model.StorageFarmItem;
import com.yuhtin.quotes.moneyfarm.util.EconomyHook;
import com.yuhtin.quotes.moneyfarm.util.NumberUtils;
import com.yuhtin.quotes.moneyfarm.util.TimeUtils;
import lombok.val;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StorageView extends PagedInventory {

    public StorageView() {
        super(
                "storage.main",
                "&aStorage",
                6 * 9
        );
    }

    @Override
    protected void configureViewer(PagedViewer viewer) {
        val configuration = viewer.getConfiguration();

        configuration.itemPageLimit(21);
        configuration.border(Border.of(1, 1, 2, 1));
    }

    @Override
    protected List<InventoryItemSupplier> createPageItems(PagedViewer viewer) {
        List<InventoryItemSupplier> items = new ArrayList<>();

        StorageFarm storageFarm = viewer.getPropertyMap().get("storageFarm");
        for (StorageFarmItem farmItem : storageFarm.getFarmItems()) {
            items.add(() -> {
                ItemStack item = farmItem.getItem().clone();
                ItemMeta itemMeta = item.getItemMeta();

                long nextExecuteTime = TimeUnit.SECONDS.toMillis(farmItem.getInterval());

                ArrayList<String> lore = new ArrayList<>();
                lore.add("&7Quantidade: &e" + NumberUtils.format(farmItem.getQuantity()));
                lore.add("&7Coins por item: &e" + NumberUtils.format(farmItem.getCoinsPerItem()));
                lore.add("");
                lore.add("&fCoins: &b" + NumberUtils.format(farmItem.getCoins()));
                lore.add("");
                lore.add("&ePróximo update em " + TimeUtils.formatOne(nextExecuteTime - System.currentTimeMillis()));
                lore.add("&eClique para coletar tudo!");

                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);

                return InventoryItem.of(item).defaultCallback(clickEvent -> {
                    double coins = farmItem.getCoins();
                    MoneyFarm.getInstance().getEconomyHook().depositCoins(viewer.getPlayer(), coins);

                    viewer.getPlayer().sendMessage("&aVocê coletou &2" + NumberUtils.format(coins) + " &acoins!");
                    viewer.getPlayer().closeInventory();

                    farmItem.setCoins(0);
                });
            });
        }

        return items;
    }
}
