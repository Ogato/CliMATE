package com.jogato.climate;


import com.google.firebase.database.ServerValue;

import java.util.Map;

import static com.google.firebase.database.ServerValue.TIMESTAMP;

public class History {
    private String mWeatherURL;
    private String mZipCode;
    private Map<String, String> mTimeStamp;

    public History(String weatherURL, String zipCode, Map<String, String>timeStamp){
        mWeatherURL = weatherURL;
        mZipCode = zipCode;
        mTimeStamp = timeStamp;
    }

    public String getmWeatherURL() {
        return mWeatherURL;
    }

    public String getmZipCode() {
        return mZipCode;
    }

}
