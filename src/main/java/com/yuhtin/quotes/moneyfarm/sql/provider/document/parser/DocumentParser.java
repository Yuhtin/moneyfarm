package com.yuhtin.quotes.moneyfarm.sql.provider.document.parser;

import com.yuhtin.quotes.moneyfarm.sql.provider.document.Document;

public interface DocumentParser<T> {

    T parse(Document document);

}
