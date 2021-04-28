package com.bupt.sse.group7.covid19.bluetoothDataEntity;

import org.litepal.crud.LitePalSupport;

public class MyIdentifierInfo extends LitePalSupport {
    int identifier_id;
    String my_identifier;
    String date;

    public String getMy_identifier() {
        return my_identifier;
    }

    public void setMy_identifier(String my_identifier) {
        this.my_identifier = my_identifier;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIdentifier_id() {
        return identifier_id;
    }

    public void setIdentifier_id(int identifier_id) {
        this.identifier_id = identifier_id;
    }
}