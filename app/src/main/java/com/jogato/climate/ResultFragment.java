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


import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.List;


public class ResultFragment extends Fragment {

    private static final String USER_QUERY_KEY = "location";
    private ForecastAdapter mAdapter;
    private TwoWayView mTwoWayView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_results, container, false);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        final String location = getArguments().getString(USER_QUERY_KEY);

        mTwoWayView = v.findViewById(R.id.temporary);
        mAdapter = new ForecastAdapter(getContext());
        mTwoWayView.setAdapter(mAdapter);

        WeatherSource.getInstance(getContext()).getWeatherForecast(location, new WeatherSource.ForecastListener() {
            @Override
            public void onForecastReceived(List<DayForecast> dayForecasts) {
                mAdapter.setItems(dayForecasts);
            }
        });

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

                MainActivity.mProgressBar.setVisibility(View.INVISIBLE);
                return dayView;
            }
            else {
                final DayForecast dayForecast = mDays.get(i);

                TextView date = view.findViewById(R.id.date);
                date.setText(dayForecast.getmDate());

                TextView averageTemp = view.findViewById(R.id.average_temp);
                averageTemp.setText(Integer.toString(dayForecast.getmAverageTemp()));
                MainActivity.mProgressBar.setVisibility(View.INVISIBLE);
                return view;

            }
        }
    }
}
