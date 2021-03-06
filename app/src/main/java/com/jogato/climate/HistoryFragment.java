package com.jogato.climate;

import android.content.Context;;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

        historyListView = (ListView) v.findViewById(R.id.city_listview);
        cityImageAdapter = new CityImageAdapter(getContext());
        historyListView.setAdapter(cityImageAdapter);

        if(((MainActivity)getActivity()).getSupportActionBar() != null) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("History");
        }

        CityImagesSource.getInstance(getActivity()).getCityImages(new CityImagesSource.CityImageListener() {
            @Override
            public void onCityImageResults(List<LocalHistory> cityImages) {
            if(cityImages != null && cityImages.size() > 0) {
                cityImageAdapter.setItems(cityImages);
                if (getActivity() != null) {

                    new CountDownTimer(2000, 1000){

                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            if(getFragmentManager() != null) {
                                TransitionFragment transition = (TransitionFragment) getFragmentManager().findFragmentByTag("transition");
                                if (transition != null) {
                                    getFragmentManager().beginTransaction().remove(transition).commitAllowingStateLoss();
                                    ((MainActivity) getActivity()).setDrawerAccess(true);
                                    historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            LocalHistory localHistory = (LocalHistory) adapterView.getItemAtPosition(i);
                                            ((MainActivity)getActivity()).sendResults(localHistory.getmCity().toLowerCase(), localHistory.getmState().trim());
                                        }
                                    });

                                }
                            }
                        }
                    }.start();

                }
            }
            else{
                TransitionFragment transition = (TransitionFragment) getFragmentManager().findFragmentByTag("transition");
                if(transition != null) {
                    getFragmentManager().beginTransaction().remove(transition).commitAllowingStateLoss();
                    ((MainActivity)getActivity()).setDrawerAccess(true);
                }
                Toast.makeText(getActivity(), "History is empty", Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).setDrawerAccess(true);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();

            }
            }
        });


        return v;
    }


    private class CityImageAdapter extends BaseAdapter {
        private Context mContext;
        private List<LocalHistory> imgURLs;
        private LayoutInflater mHistroyLayout;

        CityImageAdapter(Context context) {
            mContext = context;
            imgURLs = new ArrayList<>();
            mHistroyLayout = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setItems(List<LocalHistory> imgList) {
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
                LocalHistory localHistory = imgURLs.get(i);

                ImageView cityImage = (ImageView) historyView.findViewById(R.id.cityImage);
                Picasso.with(mContext)
                        .load(localHistory.getmImageURL())
                        .into(cityImage);

                TextView cityName = (TextView) historyView.findViewById(R.id.history_city);
                cityName.setText("  "+localHistory.getmCity().toUpperCase() + " " + localHistory.getmState().toUpperCase()+ "  ");

                return historyView;
            }
            else {
                LocalHistory localHistory = imgURLs.get(i);
                ImageView cityImage = (ImageView) view.findViewById(R.id.cityImage);
                Picasso.with(mContext)
                        .load(localHistory.getmImageURL())
                        .into(cityImage);

                TextView cityName = (TextView) view.findViewById(R.id.history_city);
                cityName.setText("  "+localHistory.getmCity().toUpperCase() + " " + localHistory.getmState().toUpperCase()+ "  ");
                return view;
            }
        }
    }
}
