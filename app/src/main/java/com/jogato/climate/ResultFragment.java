package com.jogato.climate;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class ResultFragment extends Fragment {

    private static final String USER_CITY_KEY = "city";
    private static final String USER_STATE_KEY = "state";
    private ForecastAdapter mAdapter;
    private ClothesAdapter mAdapter2;
    private TwoWayView mTwoWayView1;
    private TwoWayView mTwoWayView2;
    private ProgressBar mWeatherProgressBar;
    private ProgressBar mClothingProgressBar;
    private List<String>clothingURLs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_results, container, false);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        String city = getArguments().getString(USER_CITY_KEY);
        String state = getArguments().getString(USER_STATE_KEY);
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


        WeatherSource.getInstance(getContext()).getWeatherForecast(city, state, new WeatherSource.ForecastListener() {
            @Override
            public void onForecastReceived(List<DayForecast> dayForecasts) {
                if (dayForecasts != null) {
                    mAdapter.setItems(dayForecasts);
                    mWeatherProgressBar.setVisibility(View.INVISIBLE);
                    WeatherSource.getInstance(getContext()).getClothing(new WeatherSource.ClothingListener() {
                        @Override
                        public void onClothingReceived(List<String> imageURLs) {
                            mAdapter2.setItems(imageURLs);
                            mClothingProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                else{
                    mWeatherProgressBar.setVisibility(View.INVISIBLE);
                    mClothingProgressBar.setVisibility(View.INVISIBLE);
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
                        if(!dataSnapshot.exists()) {
                            Log.i("JO_INFO", "NEW");
                            databaseReference.child("users").child(User.getInstance().getmUserId())
                                    .child("history").setValue(User.getInstance().getmUserHistory());
                        }
                        else{
                            Log.i("JO_INFO", "Not New");
                            databaseReference.child("users").child(User.getInstance().getmUserId())
                                    .child("history").setValue(histories);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        final int []imageArray={R.drawable.city1,R.drawable.city2,R.drawable.city3,R.drawable.city4,R.drawable.city5,R.drawable.city6,R.drawable.city7,R.drawable.city8};


        //Randomly select an image for the city
        Random randomGenerator = new Random();
        int randomNumber = randomGenerator.nextInt(imageArray.length-1);


        //cityImageView.setImageResource(imageArray[randomNumber]);

        return v;
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

            if (view == null) {
                View dayView = mForecastListLayout.inflate(R.layout.list_week_forecast, viewGroup, false);
                final DayForecast dayForecast = mDays.get(i);

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
                final DayForecast dayForecast = mDays.get(i);

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
            mImages.addAll(imageList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
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
                View dayView = mForecastListLayout.inflate(R.layout.list_week_clothing, viewGroup, false);
                final String url = mImages.get(i);


                NetworkImageView clothing_icon = dayView.findViewById(R.id.clothing_img);
                ImageLoader imageLoader = WeatherSource.getInstance(getContext()).getImageLoader();
                clothing_icon.setImageUrl(url, imageLoader);
                return dayView;
            }
            else {
                final String url = mImages.get(i);
                NetworkImageView clothing_icon = view.findViewById(R.id.clothing_img);
                ImageLoader imageLoader = WeatherSource.getInstance(getContext()).getImageLoader();
                clothing_icon.setImageUrl(url, imageLoader);
                return view;
            }
        }
    }
}
