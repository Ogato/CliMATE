package com.jogato.climate;

/**
 * Created by hlomotey on 8/13/17.
 */

public class LocalHistory {
    private String mCity;
    private String mState;
    private String mFlickerURL;
    private String mImageURL;

    LocalHistory(String city, String state, String image){
        mCity = city;
        mState = state;
        mFlickerURL = image;
    }

    public String getmCity() {
        return mCity;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    public String getmState() {
        return mState;
    }

    public void setmState(String mState) {
        this.mState = mState;
    }

    public String getmFlickerURL() {
        return mFlickerURL;
    }

    public void setmFlickerURL(String mFlickerURL) {
        this.mFlickerURL = mFlickerURL;
    }

    public String getmImageURL() {
        return mImageURL;
    }

    public void setmImageURL(String mImageURL) {
        this.mImageURL = mImageURL;
    }
}
