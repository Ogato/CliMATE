package com.jogato.climate;


import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class User {
    private String mUserName;
    private String mUserEmail;
    private String mUserPreference;
    private String mUserId;
    private static Map<String, History> mUserHistory;
    private static User sUser;

    public static User getInstance(){
        if(sUser == null){
            sUser = new User();
        }
        return sUser;
    }

    public void setHistoryAndPrefs(){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query getHistory = databaseReference.child("users").child(mUserId)
                .child("history");
        getHistory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.i("JO_INFO", "OLD MAP");
                    mUserHistory = (Map<String, History>) dataSnapshot.getValue();
                }
                else{
                    Log.i("JO_INFO", "NEW MAP");
                    mUserHistory = new HashMap<String, History>();
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Query getPrefs = databaseReference.child("users").child(mUserId)
                .child("preference");
        getPrefs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.i("JO_INFO", "OLD Preference");
                    sUser.setmUserPreference(dataSnapshot.getValue().toString());
                }
                else{
                    Log.i("JO_INFO", "New Preference");
                    sUser.setmUserPreference("");
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

    public String getmUserPreference() {
        return mUserPreference;
    }

    public void setmUserPreference(String mUserPreference) {
        this.mUserPreference = mUserPreference;
    }

    public Map<String,History> getmUserHistory() {
        return mUserHistory;
    }


    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public void updateInfo(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query getNewInfo = databaseReference.orderByChild("id").equalTo(mUserId);
        getNewInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot>snapshots = dataSnapshot.getChildren();
                for(DataSnapshot s : snapshots){
                    mUserName = s.child("name").getValue().toString();
                    mUserEmail = s.child("email").getValue().toString();
                    mUserPreference = s.child("preference").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
