package com.yuhtin.quotes.moneyfarm.dao.repository;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.yuhtin.quotes.moneyfarm.dao.repository.adapter.StorageAdapter;
import com.yuhtin.quotes.moneyfarm.model.StorageFarm;
import com.yuhtin.quotes.moneyfarm.model.StorageFarmItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public final class AccountRepository {

    private static final String TABLE = "moneyfarm_data";

    @Getter
    private final SQLExecutor sqlExecutor;

    public void createTable() {
        sqlExecutor.updateQuery("CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                "owner CHAR(36) NOT NULL PRIMARY KEY," +
                "data LONGTEXT NOT NULL DEFAULT 0" +
                ");"
        );
    }

    public void recreateTable() {
        sqlExecutor.updateQuery("DELETE FROM " + TABLE);
        createTable();
    }

    private StorageFarm selectOneQuery(String query) {
        return sqlExecutor.resultOneQuery(
                "SELECT * FROM " + TABLE + " " + query,
                statement -> {
                },
                StorageAdapter.class
        );
    }

    public StorageFarm selectOne(String owner) {
        return selectOneQuery("WHERE owner = '" + owner + "'");
    }

    public Set<StorageFarm> selectAll(String query) {
        return sqlExecutor.resultManyQuery(
                "SELECT * FROM " + TABLE + " " + query,
                k -> {
                },
                StorageAdapter.class
        );
    }

    public void saveOne(String nickname, StorageFarm account) {
        this.sqlExecutor.updateQuery(
                String.format("REPLACE INTO %s VALUES(?,?,?,?,?,?)", TABLE),
                statement -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (StorageFarmItem farmItem : account.getFarmItems()) {
                        stringBuilder.append(farmItem.toString()).append("@");
                    }

                    statement.set(1, nickname);
                    statement.set(2, stringBuilder.toString());
                }
        );
    }

}
