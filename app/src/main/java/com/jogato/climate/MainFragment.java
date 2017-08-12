package com.jogato.climate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class MainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_main, container, false);
        if(getActivity() != null && !((MainActivity)getActivity()).getSupportActionBar().isShowing()){
            ((MainActivity)getActivity()).getSupportActionBar().show();
        }

        return v;
    }

}
