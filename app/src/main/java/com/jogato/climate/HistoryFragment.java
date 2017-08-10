package com.jogato.climate;

import android.content.Context;;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Fragment {
    private ListView historyListView;
    private CityImageAdapter cityImageAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        historyListView = v.findViewById(R.id.city_listview);
        cityImageAdapter = new CityImageAdapter(getContext());
        historyListView.setAdapter(cityImageAdapter);


        CityImagesSource.getInstance(getActivity()).getCityImages(new CityImagesSource.CityImageListener() {
            @Override
            public void onCityImageResults(List<String> cityImages) {
                cityImageAdapter.setItems(cityImages);
                TransitionFragment transition = (TransitionFragment) getActivity().getSupportFragmentManager().findFragmentByTag("transition");
                getActivity().getSupportFragmentManager().beginTransaction().remove(transition).commit();
            }
        });

        return v;
    }

    private class CityImageAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> imgURLs;
        private LayoutInflater mHistroyLayout;

        CityImageAdapter(Context context) {
            mContext = context;
            imgURLs = new ArrayList<>();
            mHistroyLayout = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setItems(List<String> imgList) {
            imgURLs.clear();
            imgURLs.addAll(imgList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return imgURLs.size();
        }

        @Override
        public Object getItem(int i) {
            return imgURLs.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                View historyView = mHistroyLayout.inflate(R.layout.list_history_pics, viewGroup, false);
                String imageURL = imgURLs.get(i);
                ImageView cityImage = historyView.findViewById(R.id.cityImage);

                Picasso.with(mContext)
                        .load(imageURL)
                        .into(cityImage);

                return historyView;
            } else {
                String imageURL = imgURLs.get(i);
                ImageView cityImage = view.findViewById(R.id.cityImage);
                Picasso.with(mContext)
                        .load(imageURL)
                        .into(cityImage);
                return view;

            }
        }
    }
}
