package com.jogato.climate;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
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

    public void deleteOldQuery(){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference newqueryReference = databaseReference.child("forecasts");
        Query queryReference = newqueryReference.orderByChild("Date");
        queryReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnap = dataSnapshot.getChildren();
                for (DataSnapshot snap : dataSnap){
                    Log.i("AS",snap.child("userId").getValue() + "");
                    if ((snap.child("userId").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))){
                        Log.i("JO_INFO", "delete");
                        String key = newqueryReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getKey();
                        databaseReference.child(key).removeValue();
                        break;
                    }
                    else{
                        Log.i("JO_INFO", "nope");
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
