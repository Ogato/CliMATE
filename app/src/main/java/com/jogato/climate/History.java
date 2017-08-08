package com.jogato.climate;


public class History {
    private String mWeatherURL;
    private String mZipCode;

    public History(String weatherURL, String zipCode){
        mWeatherURL = weatherURL;
        mZipCode = zipCode;
    }

    public String getmWeatherURL() {
        return mWeatherURL;
    }

    public String getmZipCode() {
        return mZipCode;
    }

}
