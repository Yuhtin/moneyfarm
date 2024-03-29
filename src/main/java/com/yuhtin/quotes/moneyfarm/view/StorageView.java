package com.yuhtin.quotes.moneyfarm.view;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.border.Border;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import com.yuhtin.quotes.moneyfarm.MoneyFarm;
import com.yuhtin.quotes.moneyfarm.model.StorageFarm;
import com.yuhtin.quotes.moneyfarm.model.StorageFarmItem;
import com.yuhtin.quotes.moneyfarm.util.ColorUtil;
import com.yuhtin.quotes.moneyfarm.util.ItemBuilder;
import com.yuhtin.quotes.moneyfarm.util.NumberUtils;
import com.yuhtin.quotes.moneyfarm.util.TimeUtils;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StorageView extends PagedInventory {

    private static StorageView instance;

    public StorageView() {
        super(
                "storage.main",
                "&8Suas Farms",
                6 * 9
        );

        getConfiguration().secondUpdate(1);
    }

    public static StorageView singleton() {
        if (instance == null) instance = new StorageView().init();
        return instance;
    }

    @Override
    protected void configureViewer(PagedViewer viewer) {
        val configuration = viewer.getConfiguration();

        configuration.itemPageLimit(21);
        configuration.border(Border.of(1, 1, 1, 1));
    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {
        StorageFarm storageFarm = viewer.getPropertyMap().get("storageFarm");

        double farmCount = 0;
        double coinsCount = 0;
        for (StorageFarmItem farmItem : storageFarm.getFarmItems()) {
            farmCount += farmItem.getQuantity();
            coinsCount += farmItem.getCoins();
        }

        editor.setItem(4, InventoryItem.of(new ItemBuilder(Material.SIGN)
                        .name("&aSuas Informações")
                        .setLore(
                                "",
                                "&fFarms: &e" + storageFarm.getFarmItems().size() + " tipo(s)",
                                "&fFarms Totais: &e" + NumberUtils.format(farmCount) + " farm(s)",
                                "&fCoins: &e" + NumberUtils.format(coinsCount) + " coins",
                                "",
                                "&aClique aqui para coletar tudo!"
                        )
                        .wrap())
                .defaultCallback(callback -> {
                    double coins = 0;
                    for (StorageFarmItem farmItem : storageFarm.getFarmItems()) {
                        coins += farmItem.getCoins();
                        farmItem.setCoins(0);
                    }

                    if (coins <= 0) {
                        viewer.getPlayer().sendMessage(ColorUtil.colored("&cVocê não tem coins para coletar!"));
                        return;
                    }

                    MoneyFarm.getInstance().getEconomyHook().depositCoins(viewer.getPlayer(), coins);
                    viewer.getPlayer().sendMessage(ColorUtil.colored("&aVocê coletou &2" + NumberUtils.format(coins) + " &acoins!"));
                }));
    }

    @Override
    protected List<InventoryItemSupplier> createPageItems(PagedViewer viewer) {
        List<InventoryItemSupplier> items = new ArrayList<>();

        StorageFarm storageFarm = viewer.getPropertyMap().get("storageFarm");
        for (StorageFarmItem farmItem : storageFarm.getFarmItems()) {
            items.add(() -> {
                ItemStack item = farmItem.getItem().clone();
                ItemMeta itemMeta = item.getItemMeta();

                long delay = TimeUnit.SECONDS.toMillis(farmItem.getInterval());
                long nextExecuteTime = delay + farmItem.getLastGenerationTime();

                ArrayList<String> lore = new ArrayList<>();
                lore.add("&7Quantidade: &e" + NumberUtils.format(farmItem.getQuantity()));
                lore.add("&7Coins por item: &e" + NumberUtils.format(farmItem.getCoinsPerItem()));
                lore.add("&7Tempo para gerar: &e" + TimeUtils.formatOne(delay));
                lore.add("");
                lore.add("&fCoins: &b" + NumberUtils.format(farmItem.getCoins()));
                lore.add("");
                lore.add("&ePróximo update em " + TimeUtils.formatOne(nextExecuteTime - System.currentTimeMillis()));
                lore.add("&eClique para coletar tudo!");

                itemMeta.setLore(ColorUtil.colored(lore));
                item.setItemMeta(itemMeta);

                return InventoryItem.of(item).defaultCallback(clickEvent -> {
                    double coins = farmItem.getCoins();
                    if (coins <= 0) {
                        viewer.getPlayer().sendMessage(ColorUtil.colored("&cVocê não tem coins para coletar!"));
                        return;
                    }

                    MoneyFarm.getInstance().getEconomyHook().depositCoins(viewer.getPlayer(), coins);
                    farmItem.setCoins(0);

                    viewer.getPlayer().sendMessage(ColorUtil.colored("&aVocê coletou &2" + NumberUtils.format(coins) + " &acoins!"));
                    update(viewer, viewer.getEditor());
                });
            });
        }

        return items;
    }

    @Override
    protected void update(PagedViewer viewer, InventoryEditor editor) {
        configureInventory(viewer, editor);
    }
}
