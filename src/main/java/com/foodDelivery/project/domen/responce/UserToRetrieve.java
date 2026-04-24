package com.foodDelivery.project.domen.responce;

public class UserToRetrieve {

    private String userName;

    private String email;

    public UserToRetrieve() {
    }

    public UserToRetrieve(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }
}
