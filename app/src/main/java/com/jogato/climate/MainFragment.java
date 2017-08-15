package com.jogato.climate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {
    private static final String USER_CITY_KEY = "city";
    private static final String USER_STATE_KEY = "state";


    private List<Button>mPopularDestinations;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_main, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("CliMATE");
        if(getActivity() != null && !((MainActivity)getActivity()).getSupportActionBar().isShowing()){
            ((MainActivity)getActivity()).getSupportActionBar().show();


        }

        mPopularDestinations = new ArrayList<>();
        mPopularDestinations.add((Button)v.findViewById(R.id.imageView1));
        mPopularDestinations.add((Button)v.findViewById(R.id.imageView2));
        mPopularDestinations.add((Button)v.findViewById(R.id.imageView3));
        mPopularDestinations.add((Button)v.findViewById(R.id.imageView4));
        mPopularDestinations.add((Button)v.findViewById(R.id.imageView5));
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        for(Button b : mPopularDestinations){
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] destination = ((Button)view).getText().toString().trim().split(",");
                    ((MainActivity)getActivity()).sendResults(destination[0].toLowerCase(), destination[1].trim());
                }
            });
        }
    }
}
