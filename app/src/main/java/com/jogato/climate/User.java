package com.jogato.climate;

import com.google.firebase.auth.FirebaseAuth;


public class User {
    private String mUserName;
    private String mUserEmail;
    private String mUserID;
    private static FirebaseAuth mUserAuthState;
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

    public String getmUserID() {
        return mUserID;
    }

    public void setmUserID(String mUserID) {
        this.mUserID = mUserID;
    }

    public FirebaseAuth getmUserAuthState() {
        return mUserAuthState;
    }

    public void setmUserAuthState(FirebaseAuth auth) {
        mUserAuthState = auth;
    }

}
