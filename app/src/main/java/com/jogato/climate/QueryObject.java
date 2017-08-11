package com.jogato.climate;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by asapkota on 8/10/17.
 */

public class QueryObject {

    protected String mUserId;
    protected String mUserName;
    protected String mDay;
    protected String mMonth;
    protected String mYear;
    protected String mCity;
    protected String mState;
    protected String mfullDate;

    public QueryObject(DataSnapshot dataSnapshot){
        mUserId = dataSnapshot.child("userId").getValue().toString();
        mUserName = dataSnapshot.child("userName").getValue().toString();
        mDay = dataSnapshot.child("day").getValue().toString();
        mMonth = dataSnapshot.child("month").getValue().toString();
        mYear = dataSnapshot.child("year").getValue().toString();
        mCity = dataSnapshot.child("city").getValue().toString();
        mState = dataSnapshot.child("state").getValue().toString();
        mfullDate = dataSnapshot.child("Date").getValue().toString();
    }

    public String getUserName(){
        return mUserName;
    }

    public String getDay(){
        return mDay;
    }

    public String getMonth(){
        return mMonth;
    }

    public String getYear(){
        return mYear;
    }

    public String getCity(){
        return mCity;
    }

    public String getState(){
        return mState;
    }

    public String getFullDate(){
        return mfullDate;
    }


}
