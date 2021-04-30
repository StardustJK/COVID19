package com.bupt.sse.group7.covid19.model;

public class UserTrip {
    String date;
    String typeNo;//飞机Kxxx
    String noSub;//车厢
    String startPos;//起始站
    String endPos;//结束站
    String memo;//备注

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

    public String getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(String typeNo) {
        this.typeNo = typeNo;
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
