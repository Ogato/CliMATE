package com.jogato.climate;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class WeatherSource {
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
    private static List<String> hot_weather_clothing;
    private static List<String> mild_weather_clothing;
    private static List<String> cold_weather_clothing;

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
            hot_weather_clothing = new ArrayList<>();
            mild_weather_clothing = new ArrayList<>();
            cold_weather_clothing = new ArrayList<>();

            hot_weather_clothing.add("shorts");
            hot_weather_clothing.add("tshirt");
            hot_weather_clothing.add("sandals");
            hot_weather_clothing.add("sunglasses");
            hot_weather_clothing.add("hat");

            mild_weather_clothing.add("jeans");
            mild_weather_clothing.add("tshirt");
            mild_weather_clothing.add("shoes");
            mild_weather_clothing.add("light coat");
            mild_weather_clothing.add("hat");

            cold_weather_clothing.add("jeans");
            cold_weather_clothing.add("heavy coat");
            cold_weather_clothing.add("boots");
            cold_weather_clothing.add("gloves");
            cold_weather_clothing.add("scarf");

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

    public void getWeatherForecast(String city, String state,final ForecastListener forecastListener){
        getZipCode(city, state, new ZipcodeListener() {
            @Override
            public void onZipCodeReceived(String zip) {
                if (!zip.isEmpty()) {
                    String url = APIXU_URL + zip + "&days=7";
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            List<DayForecast> dayForecasts = new ArrayList<>();
                            try {
                                JSONArray forecasts = response.getJSONObject("forecast").getJSONArray("forecastday");
                                for (int i = 0; i < forecasts.length(); i++) {
                                    JSONObject day = forecasts.getJSONObject(i);
                                    DayForecast dayForecast = new DayForecast(day);
                                    if (i == 0) {
                                        dayForecast.setmDate("Today");
                                    }
                                    dayForecasts.add(dayForecast);
                                }
                                forecastListener.onForecastReceived(dayForecasts);
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
        String url = "";
        List<String>typeOfWeatherList;
        if(average_temp >= 80) {
            typeOfWeatherList = hot_weather_clothing;
        }
        else if(average_temp >= 60 && average_temp < 80) {
            typeOfWeatherList = mild_weather_clothing;
        }
        else {
            typeOfWeatherList = cold_weather_clothing;
        }
        for(String clothing : typeOfWeatherList){
            //currently specified API request for men's clothing id

            url = WALMART_API_URL + clothing + "&format=json&categoryId=" + MEN_CLOTHING_ID;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("items");
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
                    Toast.makeText(mContext, "Unable to retrieve clothing suggestions at this time", Toast.LENGTH_SHORT).show();
                }
            });

            mRequestQueue.add(jsonObjectRequest);
        }


    }


    public ImageLoader getImageLoader(){ return mImageLoader; }
    public List<String> getClothingImages(){ return mClothingURLs; }

}
