package com.yuhtin.quotes.moneyfarm.sql.provider.document.parser.impl;

import com.yuhtin.quotes.moneyfarm.model.StorageFarm;
import com.yuhtin.quotes.moneyfarm.model.StorageFarmItem;
import com.yuhtin.quotes.moneyfarm.sql.provider.document.Document;
import com.yuhtin.quotes.moneyfarm.sql.provider.document.parser.DocumentParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StorageDocumentParser implements DocumentParser<StorageFarm> {

    @Getter private static final StorageDocumentParser instance = new StorageDocumentParser();

    @Override
    public StorageFarm parse(Document document) {
        String data = document.getString("data");

        StorageFarm storageFarm = new StorageFarm();
        for (String item : data.split("@")) {
            StorageFarmItem farmItem = StorageFarmItem.fromString(item);
            if (farmItem == null) continue;

            storageFarm.getFarmItems().add(farmItem);
        }

        return storageFarm;
    }

}
