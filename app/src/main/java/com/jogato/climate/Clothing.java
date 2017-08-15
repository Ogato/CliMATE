package com.jogato.climate;


public class Clothing {
    private double mPrice;
    private String mDescription;
    private String mImageURL;
    private String mProductURL;

    Clothing(double price, String description, String imageURL, String productURL){
        mPrice = price;
        mDescription = description;
        mImageURL = imageURL;
        mProductURL = productURL;
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
