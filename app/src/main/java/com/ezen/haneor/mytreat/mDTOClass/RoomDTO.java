package com.ezen.haneor.mytreat.mDTOClass;

public class RoomDTO {
    String title;
    String pw;
    String sub;
    String manager;
    String managerUID;
    String eventCode;
    int joinInUser;
    String startTime;
    String endTime;

    public RoomDTO() {}

    public RoomDTO(String title, String pw, String sub, String manager, String managerUID, String eventCode, int joinInUser, String startTime) {
        this.title = title;
        this.pw = pw;
        this.sub = sub;
        this.manager = manager;
        this.managerUID = managerUID;
        this.eventCode = eventCode;
        this.joinInUser = joinInUser;
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getManagerUID() {
        return managerUID;
    }

    public void setManagerUID(String managerUID) {
        this.managerUID = managerUID;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public int getJoinInUser() {
        return joinInUser;
    }

    public void setJoinInUser(int joinInUser) {
        this.joinInUser = joinInUser;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
