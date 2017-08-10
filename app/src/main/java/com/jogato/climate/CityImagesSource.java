package com.jogato.climate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jogato on 8/10/17.
 */

public class CityImagesSource {
    private static CityImagesSource sCityImage;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private List<String>cityImages;
    private String farm, serverid, photoid, secret, title;
    private List<String>cityUrlList;
    private String[] city_state;

    public interface CityImageListener{
        void onCityImageResults(List<String>cityImageURL);
    }

    private interface UserHistoryListener{
        void onHistoryReceived(List<String>cityUrl, String[]cityState);
    }

    public static CityImagesSource getInstance(Context context){
        if(sCityImage == null){
            sCityImage = new CityImagesSource(context);
        }
        return sCityImage;
    }

    CityImagesSource(Context context){
        cityUrlList = new ArrayList<>();
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(mContext);
    }

    public void getCityImages(final CityImageListener cityImageListener){
        cityImages = new ArrayList<>();
        getUserHistory(new UserHistoryListener() {
            @Override
            public void onHistoryReceived(List<String> cityUrl, final String[] cityState) {
                final List<Bitmap> img = new ArrayList<Bitmap>();
                //final String url = cityUrl.get(0);
                for (String url : cityUrl) {
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                //  name = response.getString("photos");
                                JSONObject photos = response.getJSONObject("photos");
                                JSONArray photo = photos.getJSONArray("photo");

                                JSONObject firstObj = photo.getJSONObject(0);
                                farm = firstObj.getString("farm");
                                serverid = firstObj.getString("server");
                                photoid = firstObj.getString("id");
                                secret = firstObj.getString("secret");
                                title = firstObj.getString("title");

                                String img_url = "https://farm" + farm + ".staticflickr.com/" + serverid + "/" + photoid + "_" + secret + "_c.jpg";
                                cityImages.add(img_url);
//                                if (mContext != null) {
//                                    try {
//                                        Picasso.with(mContext).load(img_url).into(new Target() {
//                                            @Override
//                                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                                                cityImages.add(bitmap);
//                                                Log.i("JO_INFO", cityImages.size() + "last");
//                                            }
//
//                                            @Override
//                                            public void onBitmapFailed(Drawable errorDrawable) {
//
//                                            }
//
//                                            @Override
//                                            public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                                            }
//                                        });
//
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    //cityImageListener.onCityImageResults(img);
                    mRequestQueue.add(jsonObjectRequest);
                }
            }
        });


        mRequestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                Log.i("JO_INFO", "DONE:"+request.getUrl());
                if(cityImages.size() >= cityUrlList.size()){
                    cityImageListener.onCityImageResults(cityImages);
                }
            }
        });



    }

    private void getUserHistory(final UserHistoryListener userHistoryListener){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference userHistory = databaseReference.child("users").child(User.getInstance().getmUserId()).child("history");
        userHistory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("JO_INFO", "LISTENING");
                if(dataSnapshot.exists()){
                    for(DataSnapshot s : dataSnapshot.getChildren()){
                        city_state = s.getKey().split(",");
                        cityUrlList.add("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=959d473ba8cdd5d528f0231527d99591&text=Buildings in " + city_state[0] + " " + city_state[1] + "&format=json&nojsoncallback=1");
                    }
                    userHistoryListener.onHistoryReceived(cityUrlList, city_state);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
