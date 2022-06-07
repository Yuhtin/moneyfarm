package com.yuhtin.quotes.moneyfarm.cache;

import com.yuhtin.quotes.moneyfarm.MoneyFarm;
import com.yuhtin.quotes.moneyfarm.model.FarmItem;
import com.yuhtin.quotes.moneyfarm.model.StorageFarm;
import com.yuhtin.quotes.moneyfarm.util.ItemBuilder;
import lombok.Getter;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@Getter
public class StorageCache {

    private final HashMap<String, StorageFarm> cache = new HashMap<>();
    private final HashMap<String, FarmItem> farmItems = new HashMap<>();
    private final HashMap<String, Double> generationMultipliers = new HashMap<>();
    private final HashMap<String, Double> stackLimit = new HashMap<>();

    public void init() {
        FileConfiguration config = MoneyFarm.getInstance().getConfig();
        ConfigurationSection section = config.getConfigurationSection("items");
        for (String item : section.getKeys(false)) {
            ConfigurationSection itemSection = section.getConfigurationSection(item);

            FarmItem farmItem = FarmItem.builder()
                    .identifier(itemSection.getString("name"))
                    .interval(itemSection.getInt("interval"))
                    .coinsPerItem(itemSection.getDouble("coins"))
                    .item(parse(itemSection.getConfigurationSection("item")))
                    .build();

            this.farmItems.put(farmItem.getIdentifier(), farmItem);
        }

        ConfigurationSection bonusSection = config.getConfigurationSection("bonus");
        ConfigurationSection stackSection = bonusSection.getConfigurationSection("stack");
        for (String key : stackSection.getKeys(false)) {
            this.stackLimit.put(key, stackSection.getDouble(key));
        }

        ConfigurationSection generateSection = bonusSection.getConfigurationSection("generate");
        for (String key : generateSection.getKeys(false)) {
            this.generationMultipliers.put(key, generateSection.getDouble(key));
        }
    }

    public ItemStack parse(ConfigurationSection section) {
        val builder = new ItemBuilder(
                Material.valueOf(section.getString("material", "")),
                section.getInt("data", 0)
        );

        return builder.name(section.getString("displayName")).wrap();
    }

}
