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
    private final static int IMAGE_CACHE_COUNT = 10;
    private static WeatherSource sWeatherSource;
    private RequestQueue mRequestQueue;
    private Context mContext;
    private List<DayForecast>mDayForecastList;
    private ImageLoader mImageLoader;

    public interface ForecastListener{
        void onForecastReceived(List<DayForecast>dayForecasts);
    }

    public static WeatherSource getInstance(Context context){
        if(sWeatherSource == null){
            sWeatherSource = new WeatherSource(context);
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

    public void getWeatherForecast(String location,final ForecastListener forecastListener){
        String url = APIXU_URL + location + "&days=7";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<DayForecast>dayForecasts = new ArrayList<>();
                try {
                    JSONArray forecasts = response.getJSONObject("forecast").getJSONArray("forecastday");
                    for(int i = 0; i < forecasts.length(); i++){
                        JSONObject day = forecasts.getJSONObject(i);
                        DayForecast dayForecast = new DayForecast(day);
                        dayForecasts.add(dayForecast);
                    }
                    forecastListener.onForecastReceived(dayForecasts);
                }
                catch (JSONException e){
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


    public ImageLoader getImageLoader(){ return mImageLoader; }

}
