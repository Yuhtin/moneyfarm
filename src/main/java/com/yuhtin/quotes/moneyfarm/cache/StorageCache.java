package com.yuhtin.quotes.moneyfarm.cache;

import com.yuhtin.quotes.moneyfarm.model.FarmItem;
import com.yuhtin.quotes.moneyfarm.model.StorageFarm;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class StorageCache {

    private final HashMap<String, StorageFarm> cache = new HashMap<>();
    private final HashMap<String, FarmItem> farmItem = new HashMap<>();
    private final HashMap<String, Double> generationMultipliers = new HashMap<>();
    private final HashMap<String, Double> stackLimit = new HashMap<>();

}
