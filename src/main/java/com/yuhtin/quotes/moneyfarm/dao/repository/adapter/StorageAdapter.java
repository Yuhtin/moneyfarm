package com.yuhtin.quotes.moneyfarm.dao.repository.adapter;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.yuhtin.quotes.moneyfarm.model.StorageFarm;
import com.yuhtin.quotes.moneyfarm.model.StorageFarmItem;

public final class StorageAdapter implements SQLResultAdapter<StorageFarm> {

    @Override
    public StorageFarm adaptResult(SimpleResultSet resultSet) {
        String data = resultSet.get("data");

        StorageFarm storageFarm = new StorageFarm();
        for (String item : data.split("@")) {
            StorageFarmItem farmItem = StorageFarmItem.fromString(item);
            if (farmItem == null) continue;

            storageFarm.getFarmItems().add(farmItem);
        }

        return storageFarm;
    }

}
