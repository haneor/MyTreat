package com.ezen.haneor.mytreat.mDTOClass;

public class UserDTO {
    String userName;
    String userEmail;
    String userPhoneNumber;
    String userUUID;
    String eventCode;
    String manager;
    String userRealName;
    String comment;

    public UserDTO() {}

    public UserDTO(String userName, String userEmail, String userPhoneNumber, String userUUID) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.userUUID = userUUID;
    }

    public UserDTO(String eventCode, String userRealName, String userEmail, String userPhoneNumber, String comment){
        this.eventCode = eventCode;
        this.userRealName = userRealName;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.comment = comment;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }
}
