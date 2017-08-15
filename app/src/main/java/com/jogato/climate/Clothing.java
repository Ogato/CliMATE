package com.jogato.climate;


public class Clothing {
    private double mPrice;
    private String mDescription;
    private String mImageURL;

    Clothing(double price, String description, String imageURL){
        mPrice = price;
        mDescription = description;
        mImageURL = imageURL;
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
}
