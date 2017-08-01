package com.jogato.climate;


import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class DayForecast {
    private String mDate;
    private int mMaxTemp;
    private int mMinTemp;
    private int mAverageTemp;
    private double mWindSpeed;
    private double mHumidity;
    private String mDescription;
    private String mImageViewURL;

    public DayForecast(JSONObject dayObject){
        try {
            mDate = dayObject.getString("date");
            mMaxTemp = (int)Math.round(dayObject.getDouble("maxtemp_f"));
            mMinTemp = (int)Math.round(dayObject.getDouble("mintemp_f"));
            mAverageTemp = (int)Math.round(dayObject.getDouble("avgtemp_f"));
            mWindSpeed = dayObject.getDouble("maxwind_mph");
            mHumidity = dayObject.getDouble("avghumidity");
            mDescription = dayObject.getJSONObject("condition").getString("text");
            mImageViewURL = dayObject.getJSONObject("condition").getString("icon").replace("//", "");
        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }

    public String getmDate() {
        return mDate;
    }

    public int getmMaxTemp() {
        return mMaxTemp;
    }

    public int getmMinTemp() {
        return mMinTemp;
    }

    public int getmAverageTemp() {
        return mAverageTemp;
    }

    public double getmWindSpeed() {
        return mWindSpeed;
    }

    public double getmHumidity() {
        return mHumidity;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmImageViewURL() {
        return mImageViewURL;
    }
}
