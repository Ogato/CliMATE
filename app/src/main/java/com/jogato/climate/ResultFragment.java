package com.jogato.climate;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ResultFragment extends Fragment {

    private static final String USER_CITY_KEY = "city";
    private static final String USER_STATE_KEY = "state";
    private ForecastAdapter mAdapter;
    private ClothesAdapter mAdapter2;
    private ListView mTwoWayView1;
    private ListView mTwoWayView2;
    private ProgressBar mWeatherProgressBar;
    private ProgressBar mClothingProgressBar;
    private List<String>clothingURLs;


//Variables for image view
    RequestQueue rq;
    TextView cityT, titleT;
    ImageView city_image;
    String farm, serverid, photoid, secret, title, city_text, state_text, Url;
    int imageCount = 0, totalImages;
    JsonObjectRequest offerObject;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_results_test, container, false);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        String city = getArguments().getString(USER_CITY_KEY);
        String state = getArguments().getString(USER_STATE_KEY);
        city_text = city;
        state_text = state;



        Url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=959d473ba8cdd5d528f0231527d99591&text=Buildings in " + city_text + " " + state_text + "&format=json&nojsoncallback=1";


        Log.d("Harold", Url);
        rq = Volley.newRequestQueue(getContext());

        city_image = (ImageView) v.findViewById(R.id.photo_img);
        cityT = (TextView) v.findViewById(R.id.cityText);
        titleT = (TextView) v.findViewById(R.id.titleText);

        final Handler handler = new Handler(getContext().getMainLooper());


        new Runnable() {
            int updateInterval = 6000; //=one second

            @Override
            public void run() {
                //android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                if (imageCount != totalImages) {
                    Log.i("ImageCount true state", imageCount + "");
                    Log.i("totalImages true state", totalImages + "");

                    imageCount += 1;
                    sendJsonRequest();
                    String count = String.valueOf(imageCount);
                    Log.d("Harold", count);
                } else {
                    Log.i("ImageCount -RUN method", imageCount + "");
                    Log.i("TotalImages -RUN method", totalImages + "");
                    Log.i("Is image count 0", "Seems so");
                    imageCount = 0;
                }

                handler.postDelayed(this, updateInterval);

            }
        }.run();


//        for (int i = 0; i < 10; i++){
//            if (imageCount != totalImages){
//                Log.i("ImageCount", imageCount + "");
//                Log.i("TotalImages", totalImages + "");
//                imageCount += 1;
//                sendJsonRequest();
//            }else{
//                if (i == 0){
//                    sendJsonRequest();
//                }
//                Log.i("ImageCount -RUN method", imageCount + "");
//                Log.i("TotalImages -RUN method", totalImages + "");
//                imageCount = 0;
//            }
//        }







        Log.i("JO_INFO", city + " " + state);
        //ImageView cityImageView = v.findViewById(R.id.city_image);

        mTwoWayView1 = v.findViewById(R.id.temporary);
        mTwoWayView2 = v.findViewById(R.id.temporary1);
        mWeatherProgressBar = v.findViewById(R.id.weather_loading);
        mWeatherProgressBar.setVisibility(View.VISIBLE);
        mClothingProgressBar = v.findViewById(R.id.clothing_loading);
        mClothingProgressBar.setVisibility(View.VISIBLE);

        mAdapter = new ForecastAdapter(getContext());
        mAdapter2 = new ClothesAdapter(getContext());

        mTwoWayView1.setAdapter(mAdapter);
        mTwoWayView2.setAdapter(mAdapter2);

        sendJsonRequest();


        WeatherSource.getInstance(getContext()).getWeatherForecast(city, state, new WeatherSource.ForecastListener() {
            @Override
            public void onForecastReceived(List<DayForecast> dayForecasts) {
                if (dayForecasts != null) {
                    mAdapter.setItems(dayForecasts);
                    mWeatherProgressBar.setVisibility(View.INVISIBLE);
                    WeatherSource.getInstance(getContext()).getClothing(new WeatherSource.ClothingListener() {
                        @Override
                        public void onClothingReceived(List<String> imageURLs) {
                            Log.i("ResultFragment", imageURLs.toString());
                            mAdapter2.setItems(imageURLs);
                            mTwoWayView2.setAdapter(mAdapter2);
                            mClothingProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                else{
                    mWeatherProgressBar.setVisibility(View.INVISIBLE);
                    mClothingProgressBar.setVisibility(View.INVISIBLE);
                    TransitionFragment transition = (TransitionFragment)getActivity().getSupportFragmentManager().findFragmentByTag("transition");
                    getActivity().getSupportFragmentManager().beginTransaction().remove(transition).commit();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(container != null ? container.getId() : R.id.container, new MainFragment(), "main").commit();
                }
            }
        }, new WeatherSource.HistoryListener(){
            @Override
            public void onHistoryChanged(final Map<String, History> histories) {
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                Query updateHistory = databaseReference.child("users").child(User.getInstance().getmUserId()).child("hisory");
                updateHistory.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            databaseReference.child("users").child(User.getInstance().getmUserId())
                                    .child("history").setValue(User.getInstance().getmUserHistory());
                        } else {
                            databaseReference.child("users").child(User.getInstance().getmUserId())
                                    .child("history").setValue(histories);
                        }

                        new CountDownTimer(2000, 1000) {
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                if(getActivity() != null) {
                                    Fragment transition = getActivity().getSupportFragmentManager().findFragmentByTag("transition");
                                    getActivity().getSupportFragmentManager().beginTransaction().remove(transition).commit();
                                }
                                else{
                                    Log.i("INFO", "NULL");
                                }
                            }

                        }.start();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        return v;
    }


    public void sendJsonRequest() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    //  name = response.getString("photos");
                    JSONObject photos = response.getJSONObject("photos");
                    JSONArray photo = photos.getJSONArray("photo");
                    totalImages = photo.length();
                    //Log.d("total image", String.valueOf(totalImages));

                    JSONObject firstObj = photo.getJSONObject(imageCount);
                    farm = firstObj.getString("farm");
                    serverid = firstObj.getString("server");
                    photoid = firstObj.getString("id");
                    secret = firstObj.getString("secret");
                    title = firstObj.getString("title");

                    String img_url = "https://farm" + farm + ".staticflickr.com/" + serverid + "/" + photoid + "_" + secret + "_c.jpg";

                    cityT.setText(city_text.toUpperCase() + " " + state_text.toUpperCase());
                    titleT.setText(title);
                    if (getActivity() != null) {
                        Picasso.with(getActivity()).load(img_url).into(city_image);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        rq.add(jsonObjectRequest);
    }

    private class ForecastAdapter extends BaseAdapter {
        private Context mContext;
        private List<DayForecast> mDays;
        private LayoutInflater mForecastListLayout;

        ForecastAdapter(Context context) {
            mContext = context;
            mDays = new ArrayList<>();
            mForecastListLayout = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setItems(List<DayForecast> forecastList) {
            mDays.clear();
            mDays.addAll(forecastList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            Log.i("Count of the days", mDays.size() + "");
            return mDays.size();
        }

        @Override
        public Object getItem(int i) {
            return mDays.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Log.i("View check", "Get view is called");
            if (view == null) {
                View dayView = mForecastListLayout.inflate(R.layout.list_week_forecast, viewGroup, false);
                DayForecast dayForecast = mDays.get(i);

                TextView date = dayView.findViewById(R.id.date);
                date.setText(dayForecast.getmDate());

                TextView averageTemp = dayView.findViewById(R.id.average_temp);
                averageTemp.setText(Integer.toString(dayForecast.getmAverageTemp()));

                NetworkImageView weather_icon = dayView.findViewById(R.id.weather_img);
                ImageLoader imageLoader = WeatherSource.getInstance(getContext()).getImageLoader();
                weather_icon.setImageUrl(dayForecast.getmImageViewURL(), imageLoader);

                return dayView;
            }
            else {
                DayForecast dayForecast = mDays.get(i);

                TextView date = view.findViewById(R.id.date);
                date.setText(dayForecast.getmDate());

                TextView averageTemp = view.findViewById(R.id.average_temp);
                averageTemp.setText(Integer.toString(dayForecast.getmAverageTemp()));
                clothingURLs = WeatherSource.getInstance(getContext()).getClothingImages();
                return view;

            }
        }
    }

    private class ClothesAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> mImages;
        private LayoutInflater mForecastListLayout;

        ClothesAdapter(Context context) {
            mContext = context;
            mImages = new ArrayList<>();
            mForecastListLayout = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setItems(List<String> imageList) {
            mImages.clear();
            Log.i("ResultFragment", imageList.toString());
            mImages.addAll(imageList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            Log.i("Count of the Images", mImages.size() + "");
            return mImages.size();
        }

        @Override
        public Object getItem(int i) {
            return mImages.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                Log.i("ResultFragment", "getView called");
                View dayView = mForecastListLayout.inflate(R.layout.list_week_clothing, viewGroup, false);
                String url = mImages.get(i);


                NetworkImageView clothing_icon = dayView.findViewById(R.id.clothing_img);
                ImageLoader imageLoader = WeatherSource.getInstance(getContext()).getImageLoader();
                clothing_icon.setImageUrl(url, imageLoader);
                return dayView;
            }
            else {
                String url = mImages.get(i);
                Log.i("ResultFragment", url);
                NetworkImageView clothing_icon = view.findViewById(R.id.clothing_img);
                ImageLoader imageLoader = WeatherSource.getInstance(getContext()).getImageLoader();
                clothing_icon.setImageUrl(url, imageLoader);
                return view;
            }
        }
    }
}
