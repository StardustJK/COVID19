package com.bupt.sse.group7.covid19.bluetoothDataEntity;

import org.litepal.crud.LitePalSupport;

public class MySecretKeyInfo extends LitePalSupport {
    private String secret_key;
    private String date;
    private boolean is_used = false; //标记是否上传过

    public String getSecret_key() {
        return secret_key;
    }

    public void setSecret_key(String secret_key) {
        this.secret_key = secret_key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isIs_used() {
        return is_used;
    }

    public void setIs_used(boolean is_used) {
        this.is_used = is_used;
    }
}
