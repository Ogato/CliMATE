package com.jogato.climate;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import org.lucasr.twowayview.TwoWayView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ResultFragment extends Fragment {

    private static final String USER_CITY_KEY = "city";
    private static final String USER_STATE_KEY = "state";
    private ForecastAdapter mAdapter;
    private ActiveWearAdapter activeWearAdapter;
    private OfficeWearAdapter officeWearAdapter;
    private CasualWearAdapter casualWearAdapter;
    private TwoWayView mTwoWayViewWeather;
    private TwoWayView mTwoWayViewActiveClothing;
    private TwoWayView mTwoWayViewOfficeClothing;
    private TwoWayView mTwoWayViewCasualClothing;

//Variables for image view
    RequestQueue rq;
    TextView cityT, titleT;
    ImageView city_image, city_trans;
    String farm, serverid, photoid, secret, title, city_text, state_text, Url;
    int imageCount = 0, totalImages;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_results, container, false);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        if(((MainActivity)getActivity()).getSupportActionBar() != null) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("CLIMATE");
        }

        final String city = getArguments().getString(USER_CITY_KEY);
        final String state = getArguments().getString(USER_STATE_KEY);
        city_text = city;
        state_text = state;

        Url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=959d473ba8cdd5d528f0231527d99591&text=Buildings in " + city_text + " " + state_text + "&format=json&nojsoncallback=1";
        rq = Volley.newRequestQueue(getContext());

        city_image = (ImageView) v.findViewById(R.id.photo_img);
        cityT = (TextView) v.findViewById(R.id.cityText);
        titleT = (TextView) v.findViewById(R.id.titleText);
        city_trans = (ImageView) v.findViewById(R.id.cityTransImage);

        new Runnable() {
            int updateInterval = 6000; //=one second

            @Override
            public void run() {

                if (imageCount != totalImages) {
                    imageCount += 1;
                    sendJsonRequest();
                    String count = String.valueOf(imageCount);
                    Log.d("Harold", count);
                } else {

                    imageCount = 0;
                }

                city_image.postDelayed(this, updateInterval);
            }
        }.run();

        mTwoWayViewWeather = (TwoWayView) v.findViewById(R.id.weather);
        mTwoWayViewActiveClothing = (TwoWayView) v.findViewById(R.id.active_wear);
        mTwoWayViewOfficeClothing = (TwoWayView) v.findViewById(R.id.office_wear);
        mTwoWayViewCasualClothing = (TwoWayView) v.findViewById(R.id.casual_wear);

        mAdapter = new ForecastAdapter(getContext());
        activeWearAdapter = new ActiveWearAdapter(getContext());
        officeWearAdapter = new OfficeWearAdapter(getContext());
        casualWearAdapter = new CasualWearAdapter(getContext());

        mTwoWayViewWeather.setAdapter(mAdapter);


        mTwoWayViewActiveClothing.setAdapter(activeWearAdapter);
        mTwoWayViewActiveClothing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Clothing clothing = (Clothing)adapterView.getItemAtPosition(i);
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(clothing.getmProductURL()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    getContext().startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // Chrome browser presumably not installed so allow user to choose instead
                    intent.setPackage(null);
                    getContext().startActivity(intent);
                }
            }
        });

        mTwoWayViewOfficeClothing.setAdapter(officeWearAdapter);
        mTwoWayViewOfficeClothing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Clothing clothing = (Clothing)adapterView.getItemAtPosition(i);
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(clothing.getmProductURL()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    getContext().startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // Chrome browser presumably not installed so allow user to choose instead
                    intent.setPackage(null);
                    getContext().startActivity(intent);
                }
            }
        });

        mTwoWayViewCasualClothing.setAdapter(casualWearAdapter);
        mTwoWayViewCasualClothing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Clothing clothing = (Clothing)adapterView.getItemAtPosition(i);
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(clothing.getmProductURL()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    getContext().startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // Chrome browser presumably not installed so allow user to choose instead
                    intent.setPackage(null);
                    getContext().startActivity(intent);
                }
            }
        });



        sendJsonRequest();

        final FragmentManager fm = getFragmentManager();
        WeatherSource.getInstance(getContext()).getWeatherForecast(city, state, new WeatherSource.ForecastListener() {
            @Override
            public void onForecastReceived(List<DayForecast> dayForecasts) {
                if (dayForecasts != null) {
                    mAdapter.setItems(dayForecasts);
                    WeatherSource.getInstance(getContext()).getActiveClothing(new WeatherSource.ActiveClothingListener() {
                        @Override
                        public void onClothingReceived(List<Clothing> activeImageURLs) {
                                activeWearAdapter.setItems(activeImageURLs);
                        }
                    });
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            WeatherSource.getInstance(getContext()).getOfficeClothing(new WeatherSource.OfficeClothingListener() {
                                @Override
                                public void onClothingReceived(List<Clothing> officeImageURLs) {
                                    officeWearAdapter.setItems(officeImageURLs);
                                }
                            });
                        }

                    }.start();

                    new CountDownTimer(2500, 1000) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            WeatherSource.getInstance(getContext()).getCasualClothing(new WeatherSource.CasualClothingListener() {
                                @Override
                                public void onClothingReceived(List<Clothing> casualImageURLs) {
                                    casualWearAdapter.setItems(casualImageURLs);
                                }
                            });
                        }

                    }.start();


                }
                else{
                    ((MainActivity)getActivity()).setDrawerAccess(true);
                    TransitionFragment transition = (TransitionFragment)fm.findFragmentByTag("transition");
                    fm.beginTransaction().remove(transition).commitAllowingStateLoss();
                    fm.beginTransaction().replace(R.id.fragment_container, new MainFragment(), "main").commitAllowingStateLoss();
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
                                TransitionFragment transition = (TransitionFragment) fm.findFragmentByTag("transition");
                                if(transition != null) {
                                    fm.beginTransaction().remove(transition).commitAllowingStateLoss();
                                    mTwoWayViewWeather.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            DayForecast forecastObject = ((DayForecast)adapterView.getItemAtPosition(i));
                                            ImageLoader imageLoader = WeatherSource.getInstance(getContext()).getImageLoader();
                                            View forecastView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_day_forecast, null);

                                            NetworkImageView weatherIcon = (NetworkImageView) forecastView.findViewById(R.id.weather_img);
                                            weatherIcon.setImageUrl(forecastObject.getmImageViewURL(), imageLoader);

                                            TextView date = (TextView) forecastView.findViewById(R.id.date);
                                            date.setText(forecastObject.getmDate());

                                            TextView averageTemp = (TextView) forecastView.findViewById(R.id.average_temp);
                                            averageTemp.setText("Average Temp: " + forecastObject.getmAverageTemp());

                                            TextView description = (TextView) forecastView.findViewById(R.id.description);
                                            description.setText("Condition: " + forecastObject.getmDescription());

                                            TextView minTemp = (TextView) forecastView.findViewById(R.id.min_temp);
                                            minTemp.setText("Min Temp: " + forecastObject.getmMinTemp());

                                            TextView maxTemp = (TextView) forecastView.findViewById(R.id.max_temp);
                                            maxTemp.setText("Max Temp: " + forecastObject.getmMaxTemp());

                                            TextView windSpeed = (TextView) forecastView.findViewById(R.id.wind_speed);
                                            windSpeed.setText("Wind Speed: " + forecastObject.getmWindSpeed());

                                            TextView humidity = (TextView) forecastView.findViewById(R.id.humidity);
                                            humidity.setText("Humidity: " + forecastObject.getmHumidity());

                                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                                    .setView(forecastView)
                                                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();
                                                        }
                                                    })
                                                    .create();

                                            alertDialog.show();
                                        }
                                    });
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

        ((MainActivity)getActivity()).setDrawerAccess(true);
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
                    Log.d("total image", String.valueOf(totalImages));

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
                        Picasso.with(getActivity()).load(img_url).noPlaceholder().into(city_image);

                        city_trans.setImageResource(R.drawable.city_trans);
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

                TextView date = (TextView) dayView.findViewById(R.id.date);
                date.setText(dayForecast.getmDate());

                TextView averageTemp = (TextView) dayView.findViewById(R.id.average_temp);
                averageTemp.setText(Integer.toString(dayForecast.getmAverageTemp()) + " " + (char) 0x00B0 +"F");

                NetworkImageView weather_icon = (NetworkImageView) dayView.findViewById(R.id.weather_img);
                ImageLoader imageLoader = WeatherSource.getInstance(getContext()).getImageLoader();
                weather_icon.setImageUrl(dayForecast.getmImageViewURL(), imageLoader);

                return dayView;
            }
            else {
                final DayForecast dayForecast = mDays.get(i);

                TextView date = (TextView) view.findViewById(R.id.date);
                date.setText(dayForecast.getmDate());

                TextView averageTemp = (TextView) view.findViewById(R.id.average_temp);
                averageTemp.setText(Integer.toString(dayForecast.getmAverageTemp()) + " " + (char) 0x00B0 +"F");
                return view;

            }
        }
    }

    private class ActiveWearAdapter extends BaseAdapter {
        private Context mContext;
        private List<Clothing> mActive;
        private LayoutInflater mForecastListLayout;

        ActiveWearAdapter(Context context) {
            mContext = context;
            mActive = new ArrayList<>();
            mForecastListLayout = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setItems(List<Clothing> imageActiveList) {
            mActive.clear();
            mActive.addAll(imageActiveList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mActive.size();
        }

        @Override
        public Object getItem(int i) {
            return mActive.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                View clothingView = mForecastListLayout.inflate(R.layout.list_week_clothing, viewGroup, false);
                Clothing clothing = mActive.get(i);
                final String url = clothing.getmImageURL();

                NetworkImageView clothing_icon = (NetworkImageView) clothingView.findViewById(R.id.clothing_img);
                ImageLoader imageLoader = WeatherSource.getInstance(getContext()).getImageLoader();
                clothing_icon.setImageUrl(url, imageLoader);

                TextView description = (TextView) clothingView.findViewById(R.id.description);
                description.setText(clothing.getmDescription());

                TextView price = (TextView) clothingView.findViewById(R.id.price);
                String strPrice = new DecimalFormat("$0.00").format(clothing.getmPrice());
                price.setText(strPrice);
                return clothingView;
            }
            else {
                Clothing clothing = mActive.get(i);
                final String url = clothing.getmImageURL();

                NetworkImageView clothing_icon = (NetworkImageView) view.findViewById(R.id.clothing_img);
                ImageLoader imageLoader = WeatherSource.getInstance(getContext()).getImageLoader();
                clothing_icon.setImageUrl(url, imageLoader);

                TextView description = (TextView) view.findViewById(R.id.description);
                description.setText(clothing.getmDescription());

                TextView price = (TextView) view.findViewById(R.id.price);
                String strPrice = new DecimalFormat("$0.00").format(clothing.getmPrice());
                price.setText(strPrice);
                return view;
            }
        }
    }

    private class OfficeWearAdapter extends BaseAdapter {
        private Context mContext;
        private List<Clothing> mOfficemages;
        private LayoutInflater mForecastListLayout;

        OfficeWearAdapter(Context context) {
            mContext = context;
            mOfficemages = new ArrayList<>();
            mForecastListLayout = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setItems(List<Clothing> imageList) {
            mOfficemages.clear();
            mOfficemages.addAll(imageList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mOfficemages.size();
        }

        @Override
        public Object getItem(int i) {
            return mOfficemages.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                View clothingView = mForecastListLayout.inflate(R.layout.list_week_clothing, viewGroup, false);
                Clothing clothing = mOfficemages.get(i);
                final String url = clothing.getmImageURL();

                NetworkImageView clothing_icon = (NetworkImageView) clothingView.findViewById(R.id.clothing_img);
                ImageLoader imageLoader = WeatherSource.getInstance(getContext()).getImageLoader();
                clothing_icon.setImageUrl(url, imageLoader);

                TextView description = (TextView) clothingView.findViewById(R.id.description);
                description.setText(clothing.getmDescription());

                TextView price = (TextView) clothingView.findViewById(R.id.price);
                String strPrice = new DecimalFormat("$0.00").format(clothing.getmPrice());
                price.setText(strPrice);
                return clothingView;
            }
            else {
                Clothing clothing = mOfficemages.get(i);
                final String url = clothing.getmImageURL();

                NetworkImageView clothing_icon = (NetworkImageView) view.findViewById(R.id.clothing_img);
                ImageLoader imageLoader = WeatherSource.getInstance(getContext()).getImageLoader();
                clothing_icon.setImageUrl(url, imageLoader);

                TextView description = (TextView) view.findViewById(R.id.description);
                description.setText(clothing.getmDescription());

                TextView price = (TextView) view.findViewById(R.id.price);
                String strPrice = new DecimalFormat("$0.00").format(clothing.getmPrice());
                price.setText(strPrice);
                return view;
            }
        }
    }

    private class CasualWearAdapter extends BaseAdapter {
        private Context mContext;
        private List<Clothing> mCasualImages;
        private LayoutInflater mForecastListLayout;

        CasualWearAdapter(Context context) {
            mContext = context;
            mCasualImages = new ArrayList<>();
            mForecastListLayout = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setItems(List<Clothing> imageList) {
            mCasualImages.clear();
            mCasualImages.addAll(imageList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mCasualImages.size();
        }

        @Override
        public Object getItem(int i) {
            return mCasualImages.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                View clothingView = mForecastListLayout.inflate(R.layout.list_week_clothing, viewGroup, false);
                Clothing clothing = mCasualImages.get(i);
                final String url = clothing.getmImageURL();

                NetworkImageView clothing_icon = (NetworkImageView) clothingView.findViewById(R.id.clothing_img);
                ImageLoader imageLoader = WeatherSource.getInstance(getContext()).getImageLoader();
                clothing_icon.setImageUrl(url, imageLoader);

                TextView description = (TextView) clothingView.findViewById(R.id.description);
                description.setText(clothing.getmDescription());

                TextView price = (TextView) clothingView.findViewById(R.id.price);
                String strPrice = new DecimalFormat("$0.00").format(clothing.getmPrice());
                price.setText(strPrice);
                return clothingView;
            }
            else {
                Clothing clothing = mCasualImages.get(i);
                final String url = clothing.getmImageURL();

                NetworkImageView clothing_icon = (NetworkImageView) view.findViewById(R.id.clothing_img);
                ImageLoader imageLoader = WeatherSource.getInstance(getContext()).getImageLoader();
                clothing_icon.setImageUrl(url, imageLoader);

                TextView description = (TextView) view.findViewById(R.id.description);
                description.setText(clothing.getmDescription());

                TextView price = (TextView) view.findViewById(R.id.price);
                String strPrice = new DecimalFormat("$0.00").format(clothing.getmPrice());
                price.setText(strPrice);
                return view;
            }
        }
    }
}
