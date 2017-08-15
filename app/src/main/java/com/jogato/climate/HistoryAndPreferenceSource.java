package com.jogato.climate;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//Source the local copy of the user's history and updated preferences
public class HistoryAndPreferenceSource {

    private static HistoryAndPreferenceSource historyAndPreferenceSource;


    public interface HistoryAndPreferencesListener{
        void onHistoryAndPreferencesResults(Boolean setPref);
    }

    //Have only 1 instance of this class
    public static HistoryAndPreferenceSource getInstance(){
        if(historyAndPreferenceSource == null){
            historyAndPreferenceSource = new HistoryAndPreferenceSource();
        }
        return historyAndPreferenceSource;
    }


    //Gets the user's history and preferences if present in the database
    public void setHistoryAndPrefs(final HistoryAndPreferencesListener historyAndPreferencesListener){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference getHistory = databaseReference.child("users").child(User.getInstance().getmUserId())
                .child("history");
        getHistory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User.getInstance().setmUserHistory((Map<String, History>) dataSnapshot.getValue());
                }
                else{
                    User.getInstance().setmUserHistory(new HashMap<String, History>());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                historyAndPreferencesListener.onHistoryAndPreferencesResults(null);
            }
        });
        DatabaseReference getPrefs = databaseReference.child("users").child(User.getInstance().getmUserId())
                .child("preference");
        getPrefs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User.getInstance().setmUserPreference(dataSnapshot.getValue(String.class));
                    historyAndPreferencesListener.onHistoryAndPreferencesResults(true);
                }
                else{
                    User.getInstance().setmUserPreference("");
                    historyAndPreferencesListener.onHistoryAndPreferencesResults(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                historyAndPreferencesListener.onHistoryAndPreferencesResults(null);
            }
        });

    }


    //Update the user's local account preferences from account page
    public void updateInfo(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query getNewInfo = databaseReference.orderByChild("id").equalTo(User.getInstance().getmUserId());
        getNewInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot>snapshots = dataSnapshot.getChildren();
                for(DataSnapshot s : snapshots){
                    User.getInstance().setmUserName(s.child("name").getValue(String.class));
                    User.getInstance().setmUserEmail(s.child("email").getValue(String.class));
                    User.getInstance().setmUserPreference(s.child("preference").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void deleteHandledData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            final String userId = user.getUid();
            final String userName = user.getEmail();
            DatabaseReference newforecastRef = databaseReference.child("forecasts");
            Query forecastRef = newforecastRef.orderByChild("Date");
            forecastRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                    for (DataSnapshot snap : dataSnapshots) {
                        Log.i("JO_INFO", "USER: " + userId);
                        Log.i("JO_INFO", "DATABSE USER: " + snap.child("userId").toString());
                        if ((snap.child("userId").getValue(String.class).equals(userId))) {
                            snap.child("day").getRef().setValue(null);
                            snap.child("month").getRef().setValue(null);
                            snap.child("year").getRef().setValue(null);
                            snap.child("city").getRef().setValue(null);
                            snap.child("state").getRef().setValue(null);
                            snap.child("Date").getRef().setValue(null);
                            snap.child("userId").getRef().setValue(null);
                            snap.child("userName").getRef().setValue(null);
                            break;
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("JO_INFO", databaseError.getMessage());
                }
            });
        }
    }
}
