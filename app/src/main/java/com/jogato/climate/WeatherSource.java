package com.jogato.climate;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WeatherSource{
    private static final String APIXU_URL = "https://api.apixu.com/v1/forecast.json?key=606241cc9f67477abce55230172107&q=";
    private static final String ZIPCODE_URL_KEY =
            "https://www.zipcodeapi.com/rest/7qajuUm9ZbseLSnqG2UT38PKy9qbHZxPqI8VCu4CGxRsqfS7hM1lVQ5uZwFHvdwW/city-zips.json/";
    private static final String WALMART_API_URL = "http://api.walmartlabs.com/v1/search?apiKey=krqhbue4u8vb8f8z6b99vpce&query=";
    private static final String MEN_CLOTHING_ID = "5438_133197";
    private static final String WOMEN_CLOTHING_ID = "5438_133162";
    private final static int IMAGE_CACHE_COUNT = 20;

    private static WeatherSource sWeatherSource;
    private RequestQueue mRequestQueue;
    private Context mContext;
    private ImageLoader mImageLoader;
    private List<String> mClothingURLs;
    private static List<String> above_90;
    private static List<String> low_70s_high_80s;
    private static List<String> range_60s;
    private static List<String> range_50s;
    private static List<String> range_40s;
    private static List<String> just_above_freezing;
    private static List<String> below_freezing;

    public interface HistoryListener{
        void onHistoryChanged(Map<String, History> histories);
    }

    public interface ForecastListener{
        void onForecastReceived(List<DayForecast>dayForecasts);
    }

    public interface ZipcodeListener{
        void onZipCodeReceived(String zip);
    }

    public interface ClothingListener{
        void onClothingReceived(List<String>imageURLs);
    }


    public static WeatherSource getInstance(Context context){
        if(sWeatherSource == null){
            sWeatherSource = new WeatherSource(context);
            above_90 = new ArrayList<>();
            low_70s_high_80s = new ArrayList<>();
            range_60s = new ArrayList<>();
            range_50s = new ArrayList<>();
            range_40s = new ArrayList<>();
            just_above_freezing = new ArrayList<>();
            below_freezing = new ArrayList<>();

            above_90.add("shorts");
            above_90.add("tank");
            above_90.add("sandals");
            above_90.add("sunglasses");
            above_90.add("hat");

            low_70s_high_80s.add("shorts");
            low_70s_high_80s.add("jeans");
            low_70s_high_80s.add("t-shirt");
            low_70s_high_80s.add("sneakers");


            range_60s.add("jeans");
            range_60s.add("long sleeved shirt");
            range_60s.add("sneakers");
            range_60s.add("jacket");

            range_50s.add("pants");
            range_50s.add("light coat");
            range_50s.add("long sleeved shirt");
            range_50s.add("jeans");
            range_50s.add("hat");

            range_40s.add("gloves");
            range_40s.add("long sleeved shirt");
            range_40s.add("light coat");
            range_40s.add("boots");
            range_40s.add("sunglasses");
            range_40s.add("hat");
            range_40s.add("thermal underwear");

            just_above_freezing.add("jeans");
            just_above_freezing.add("long sleeved shirt");
            just_above_freezing.add("boots");
            just_above_freezing.add("gloves");
            just_above_freezing.add("coat");
            just_above_freezing.add("hat");
            just_above_freezing.add("thermal underwear");

            below_freezing.add("pants");
            below_freezing.add("long sleeved shirt");
            below_freezing.add("heavy coat");
            below_freezing.add("boots");
            below_freezing.add("gloves");
            below_freezing.add("scarf");
            below_freezing.add("thermal underwear");

        }
        return sWeatherSource;
    }

    private WeatherSource(Context context){
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(mContext);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache(){
            private final LruCache<String, Bitmap> mCache = new LruCache<>(IMAGE_CACHE_COUNT);
            @Override
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
        });
    }

    public void getWeatherForecast(final String city, final String state, final ForecastListener forecastListener, final HistoryListener historyListener){
        Log.i("WeatherSource", city + " " + state);
        getZipCode(city, state, new ZipcodeListener() {
            @Override
            public void onZipCodeReceived(String zip) {
                if (!zip.isEmpty()) {
                    final String weatherUrl = APIXU_URL + zip + "&days=7";
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.GET, weatherUrl, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            List<DayForecast> dayForecasts = new ArrayList<>();
                            try {
                                JSONArray forecasts = response.getJSONObject("forecast").getJSONArray("forecastday");
                                Log.i("Forecast check", "Requesting JSON forecasts");
                                for (int i = 0; i < forecasts.length(); i++) {
                                    JSONObject day = forecasts.getJSONObject(i);
                                    DayForecast dayForecast = new DayForecast(day);
                                    if (i == 0) {
                                        dayForecast.setmDate("Today");
                                    }
                                    dayForecasts.add(dayForecast);
                                }
                                forecastListener.onForecastReceived(dayForecasts);
                                if(dayForecasts.size() > 0) {
                                    User.getInstance().getmUserHistory().put(city + "," + state, new History(weatherUrl, ZIPCODE_URL_KEY + city + "/" + state));
                                    historyListener.onHistoryChanged(User.getInstance().getmUserHistory());
                                }
                            } catch (JSONException e) {
                                Toast.makeText(mContext, "Unable to retrieve forecast at this time", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(mContext, "Unable to retrieve forecast at this time", Toast.LENGTH_SHORT).show();
                        }
                    });
                    mRequestQueue.add(jsonObjectRequest);
                }
                else{
                    Toast.makeText(mContext, "Unable to retrieve forecast at this time", Toast.LENGTH_SHORT).show();
                    forecastListener.onForecastReceived(null);
                }
            }
        });
    }

    public void getZipCode(String city, String state, final  ZipcodeListener zipcodeListener){
        String url = ZIPCODE_URL_KEY + city + "/" + state;
        Log.i("ZIPCODE", url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("zip_codes");
                    if(jsonArray.length() > 0) {
                        zipcodeListener.onZipCodeReceived(jsonArray.getString(0));
                    }
                    else{
                        Toast.makeText(mContext, "City not found", Toast.LENGTH_SHORT).show();
                        zipcodeListener.onZipCodeReceived("");
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(mContext, "Unable to retrieve forecast at this time", Toast.LENGTH_SHORT).show();
                zipcodeListener.onZipCodeReceived("");
            }
        });

        mRequestQueue.add(jsonObjectRequest);
    }

    public void getClothing(final ClothingListener clothingListener){
        mClothingURLs = new ArrayList<>();
        int average_max_temp = DayForecast.sAverageMaxTemp;
        int average_min_temp = DayForecast.sAverageMinTemp;
        int average_temp = DayForecast.sAverageTemp;
        Log.i("Average Temperature", average_temp + "");
        String url = "";
        List<String>typeOfWeatherList;
        if(average_temp >= 90) {
            typeOfWeatherList = above_90;
        }
        else if(average_temp >= 70 && average_temp < 90) {
            typeOfWeatherList = low_70s_high_80s;
        }
        else if(average_temp >= 60 && average_temp < 70) {
            typeOfWeatherList = range_60s;
        }
        else if(average_temp >= 50 && average_temp < 60) {
            typeOfWeatherList = range_50s;
        }
        else if(average_temp >= 40 && average_temp < 50) {
            typeOfWeatherList = range_40s;
        }
        else if(average_temp >= 32 && average_temp < 40) {
            typeOfWeatherList = just_above_freezing;
        }
        else {
            typeOfWeatherList = below_freezing;
        }
        Log.i("Type of Weather List", typeOfWeatherList.get(0));
        for(String clothing : typeOfWeatherList){
            //currently specified API request for men's clothing id

            url = WALMART_API_URL + clothing + "&format=json&categoryId=" + MEN_CLOTHING_ID;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("items");
                        Log.i("JSON response", jsonArray.toString());
                        if(jsonArray.length() > 0) {
                            mClothingURLs.add(jsonArray.getJSONObject((int)Math.floor(Math.random()*jsonArray.length())).getString("mediumImage"));
                        }
                        else{
                            Toast.makeText(mContext, "Unable to retrieve clothing suggestions at this time", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                    clothingListener.onClothingReceived(mClothingURLs);
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error){
                    Toast.makeText(mContext, "Unable to retrieve clothing suggestions at this time. Test Toast", Toast.LENGTH_SHORT).show();
                }
            });

            mRequestQueue.add(jsonObjectRequest);
        }


    }


    public ImageLoader getImageLoader(){ return mImageLoader; }
    public List<String> getClothingImages(){ return mClothingURLs; }

    public static void pushDatetoSchedule(String dayDate, String monthDate, String yearDate, String city, String state){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        String userName = user.getEmail();
        DatabaseReference forecastRef = databaseReference.child("forecasts");
        DatabaseReference pushRef = forecastRef.push();
        Map<String, Object> dateMap = new HashMap<String, Object>();
        dateMap.put("userId", userId);
        dateMap.put("userName", userName);
        dateMap.put("day", dayDate);
        dateMap.put("month", monthDate);
        dateMap.put("year", yearDate);
        dateMap.put("city", city);
        dateMap.put("state", state);
        String completeDate = yearDate + "-" + monthDate + "-" + dayDate;
        dateMap.put("Date", completeDate);
        Log.i("PushAS", userId);
        pushRef.setValue(dateMap);

    }

    public static void deleteHandledData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();
        final String userName = user.getEmail();
        DatabaseReference newforecastRef = databaseReference.child("forecasts");
        Query forecastRef = newforecastRef.orderByChild("Date");
        forecastRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for (DataSnapshot snap : dataSnapshots){
                    if ((snap.child("userId").equals(userId)){
                        snap.child("day").getRef().setValue(null);
                        snap.child("month").getRef().setValue(null);
                        snap.child("year").getRef().setValue(null);
                        snap.child("city").getRef().setValue(null);
                        snap.child("state").getRef().setValue(null);
                        snap.child("Date").getRef().setValue(null);
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
