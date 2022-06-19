package com.yuhtin.quotes.moneyfarm.sql;

import com.yuhtin.quotes.moneyfarm.model.StorageFarm;
import com.yuhtin.quotes.moneyfarm.sql.provider.DatabaseProvider;
import com.yuhtin.quotes.moneyfarm.sql.provider.document.Document;
import com.yuhtin.quotes.moneyfarm.sql.provider.document.parser.impl.StorageDocumentParser;

import javax.annotation.Nullable;

public final class StorageDAO extends DatabaseProvider {

    private static final String TABLE = "moneyfarm_data";

    public void createTable() {
        update("CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                "owner CHAR(36) NOT NULL PRIMARY KEY," +
                "data LONGTEXT NOT NULL DEFAULT 0" +
                ");");
    }

    @Nullable
    public StorageFarm find(String nick) {
        Document document = query("select * from " + TABLE + " where owner = '" + nick + "';");
        if (document == null) return null;

        return document.parse(StorageDocumentParser.getInstance());
    }

    public void save(String nick, StorageFarm storageFarm) {
        update("replace into " + TABLE + " values (?, ?);", nick, storageFarm.toString());
    }

}