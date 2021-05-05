package com.bupt.sse.group7.covid19.model;

public class UserTrip {
    String date;//*
    int type;//飞机*
    String no;//G308*
    String noSub;//车厢
    String startPos;//起始站
    String endPos;//结束站
    String memo;//备注
    int id;
    boolean risk;

    public boolean getRisk() {
        return risk;
    }

    public void setRisk(boolean risk) {
        this.risk = risk;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNoSub() {
        return noSub;
    }

    public void setNoSub(String noSub) {
        this.noSub = noSub;
    }

    public String getStartPos() {
        return startPos;
    }

    public void setStartPos(String startPos) {
        this.startPos = startPos;
    }

    public String getEndPos() {
        return endPos;
    }

    public void setEndPos(String endPos) {
        this.endPos = endPos;
    }

}
