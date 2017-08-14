package com.jogato.climate;


import org.json.JSONException;
import org.json.JSONObject;

public class DayForecast{
    public static int sNumDays;
    public static int sTotalMaxTemp;
    public static int sTotalMinTemp;
    public static int sTotalAverageTemp;
    public static int sAverageMaxTemp;
    public static int sAverageTemp;
    public static int sAverageMinTemp;
    public static int sWind;
    public static int sHumidity;
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
            mDate = dayObject.getString("date").substring(5);
            mMaxTemp = (int)Math.round(dayObject.getJSONObject("day").getDouble("maxtemp_f"));
            mMinTemp = (int)Math.round(dayObject.getJSONObject("day").getDouble("mintemp_f"));
            mAverageTemp = (int)Math.round(dayObject.getJSONObject("day").getDouble("avgtemp_f"));
            mWindSpeed = dayObject.getJSONObject("day").getDouble("maxwind_mph");
            mHumidity = dayObject.getJSONObject("day").getDouble("avghumidity");
            mDescription = dayObject.getJSONObject("day").getJSONObject("condition").getString("text");
            mImageViewURL = "https:" + dayObject.getJSONObject("day").getJSONObject("condition").getString("icon");
            sNumDays++;
            sWind = (int) mWindSpeed;
            sHumidity = (int) mHumidity;
            sTotalMaxTemp += mMaxTemp;
            sTotalMinTemp += mMinTemp;
            sTotalAverageTemp += mAverageTemp;
            sAverageMaxTemp = (int) (sTotalMaxTemp / sNumDays);
            sAverageMinTemp = (int) (sTotalMinTemp / sNumDays);
            sAverageTemp = (int) (sTotalAverageTemp / sNumDays);
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

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }
}
