package com.bupt.sse.group7.covid19.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 当前登录用户
 * label取值范围 {"visitor", "hospital", "patient"}
 */
public class CurrentUser {
    private static String label;
    private static String id;

    static {
        label = "visitor";
    }

    public static String getLabel() {
        return label;
    }

    public static void setLabel(String newLabel) {
        label = newLabel;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String newId) {
        id = newId;
    }

    private static CurrentUser currentUser=null;
    public static CurrentUser getCurrentUser(){
        return currentUser;
    }
    public static void setCurrentUser(CurrentUser user){
        currentUser=user;
    }
    private String userId;
    private String phone;
    private String name;
    private int status;
    private int role;
    private boolean auth;

    public CurrentUser(String userId, String phone, String name, int status, int role,boolean auth) {
        this.userId = userId;
        this.phone = phone;
        this.name = name;
        this.status = status;
        this.role = role;
        this.auth=auth;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
