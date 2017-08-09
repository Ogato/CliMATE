package com.jogato.climate;



import java.util.Map;


public class User {
    private String mUserName;
    private String mUserEmail;
    private String mUserPreference;
    private String mUserId;
    private static Map<String, History> mUserHistory;
    private static User sUser;

    public static User getInstance(){
        if(sUser == null){
            sUser = new User();
        }
        return sUser;
    }


    public String getmUserName() {

        return mUserName;
    }

    public void setmUserName(String mUserName) {

        this.mUserName = mUserName;
    }

    public String getmUserEmail() {

        return mUserEmail;
    }

    public void setmUserEmail(String mUserEmail) {

        this.mUserEmail = mUserEmail;
    }

    public String getmUserPreference() {
        return mUserPreference;
    }

    public void setmUserPreference(String mUserPreference) {
        this.mUserPreference = mUserPreference;
    }

    public Map<String,History> getmUserHistory() {
        return mUserHistory;
    }


    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }


    public void setmUserHistory(Map<String, History> mUserHistory) {
        User.mUserHistory = mUserHistory;
    }
}
