package com.bupt.sse.group7.covid19.model;

/**
 * 当前登录用户
 * label取值范围 {"visitor", "hospital", "patient"}
 */
public class CurrentUser {
    private static String label;
    private static int id;

    static {
        label = "visitor";
    }

    public static String getLabel() {
        return label;
    }

    public static void setLabel(String newLabel) {
        label = newLabel;
    }

    public static int getId() {
        return id;
    }

    public static void setId(int newId) {
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

    public CurrentUser(String userId, String phone, String name, int status, int role) {
        this.userId = userId;
        this.phone = phone;
        this.name = name;
        this.status = status;
        this.role = role;
    }
}
