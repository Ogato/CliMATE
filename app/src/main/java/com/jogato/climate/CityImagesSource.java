package com.jogato.climate;

import android.content.Context;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CityImagesSource {
    private static CityImagesSource sCityImage;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private List<LocalHistory> mCityImages;
    private String mFarm, mServerId, mPhotoId, mSecret, mTitle;
    private List<LocalHistory> mLocalHistoryList;
    private String[] mCityState;




    //Public interface for other classes to receive image urls from the flikr api
    public interface CityImageListener{
        void onCityImageResults(List<LocalHistory>cityImageURL);
    }

    //Internal interface to listen for when the database entries have been received
    private interface UserHistoryListener{
        void onHistoryReceived(List<LocalHistory>localHistory);
    }

    //Only create one instance of this class
    public static CityImagesSource getInstance(Context context){
        if(sCityImage == null){
            sCityImage = new CityImagesSource(context);
        }
        return sCityImage;
    }

    //Internal constructor
    private CityImagesSource(Context context){
        mLocalHistoryList = new ArrayList<>();
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(mContext);
    }

    //Method to receive images from flikr api based on current user's history
    public void getCityImages(final CityImageListener cityImageListener){
        mCityImages = new ArrayList<>();
        getUserHistory(new UserHistoryListener() {
            @Override
            public void onHistoryReceived(List<LocalHistory> localHistory) {

                //Send null result if any errors have occured
                if (localHistory == null ) {
                    cityImageListener.onCityImageResults(null);
                } else {

                    //Must get 1 photo for every city-state pair in current user's history
                    for (final LocalHistory history : localHistory ) {
                        String url = history.getmFlickerURL();
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    //Grab a random photo for each URL passed
                                    JSONObject photos = response.getJSONObject("photos");
                                    JSONArray photo = photos.getJSONArray("photo");
                                    JSONObject firstObj = photo.getJSONObject((int) (Math.random() * photo.length()));

                                    mFarm = firstObj.getString("farm");
                                    mServerId = firstObj.getString("server");
                                    mPhotoId = firstObj.getString("id");
                                    mSecret = firstObj.getString("secret");
                                    mTitle = firstObj.getString("title");

                                    //Compile the image urls for processing in the fragment
                                    String img_url = "https://farm" + mFarm + ".staticflickr.com/" + mServerId + "/" + mPhotoId + "_" + mSecret + "_c.jpg";
                                    history.setmImageURL(img_url);
                                    mCityImages.add(history);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                cityImageListener.onCityImageResults(null);
                            }
                        });

                        mRequestQueue.add(jsonObjectRequest);
                    }
                }
            }
        });


        //Only send image URLS when all image's from history have been processed and clear the lists
        mRequestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                if(mCityImages.size() >= mLocalHistoryList.size()){
                    cityImageListener.onCityImageResults(mCityImages);
                    mCityImages.clear();
                    mLocalHistoryList.clear();
                    mRequestQueue.removeRequestFinishedListener(this);
                }
            }
        });
    }


    //Gets the user's last 10 search's from history entry
    private void getUserHistory(final UserHistoryListener userHistoryListener){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final Query userHistory = databaseReference.child("users").child(User.getInstance().getmUserId()).child("history").limitToLast(10);
        userHistory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot s : dataSnapshot.getChildren()){

                        // 'city,ST' -> [city, ST]
                        mCityState = s.getKey().split(",");
                        mLocalHistoryList.add(new LocalHistory(mCityState[0], mCityState[1], "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=959d473ba8cdd5d528f0231527d99591&text=Buildings in " + mCityState[0] + " " + mCityState[1] + "&format=json&nojsoncallback=1"));

                    }

                    //Send the database entries to the public getCityImages method
                    userHistoryListener.onHistoryReceived(mLocalHistoryList);
                }
                else{
                    userHistoryListener.onHistoryReceived(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                userHistoryListener.onHistoryReceived(null);
            }
        });
    }


}
