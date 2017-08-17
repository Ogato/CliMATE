package com.jogato.climate;


import org.json.JSONException;
import org.json.JSONObject;

public class Clothing {
    private double mPrice;
    private String mDescription;
    private String mImageURL;
    private String mProductURL;

    Clothing(JSONObject clothingObject){
        try {
            mImageURL = clothingObject.getString("mediumImage");
            mDescription = clothingObject.getString("name");
            mProductURL = clothingObject.getString("productUrl");
            mPrice = clothingObject.getDouble("salePrice");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public double getmPrice() {
        return mPrice;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmImageURL() {
        return mImageURL;
    }

    public String getmProductURL() {
        return mProductURL;
    }
}
