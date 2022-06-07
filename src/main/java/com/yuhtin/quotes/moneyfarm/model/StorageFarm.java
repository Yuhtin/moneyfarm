package com.yuhtin.quotes.moneyfarm.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StorageFarm {

    private final List<StorageFarmItem> farmItems = new ArrayList<>();

}
