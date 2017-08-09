package com.jogato.climate;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jogato on 8/8/17.
 */

public class HistoryAndPreferenceSource {

    private static HistoryAndPreferenceSource historyAndPreferenceSource;


    public interface HistoryAndPreferencesListener{
        void onHistoryAndPreferencesResults(Boolean setPref);
    }

    public static HistoryAndPreferenceSource getInstance(){
        if(historyAndPreferenceSource == null){
            historyAndPreferenceSource = new HistoryAndPreferenceSource();
        }
        return historyAndPreferenceSource;
    }

    public void setHistoryAndPrefs(final HistoryAndPreferencesListener historyAndPreferencesListener){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference getHistory = databaseReference.child("users").child(User.getInstance().getmUserId())
                .child("history");
        getHistory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.i("JO_INFO", "OLD MAP");
                    User.getInstance().setmUserHistory((Map<String, History>) dataSnapshot.getValue());
                }
                else{
                    Log.i("JO_INFO", "NEW MAP");
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
                    Log.i("JO_INFO", "OLD Preference");
                    User.getInstance().setmUserPreference(dataSnapshot.getValue(String.class));
                    historyAndPreferencesListener.onHistoryAndPreferencesResults(true);
                }
                else{
                    Log.i("JO_INFO", "New Preference");
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
}
