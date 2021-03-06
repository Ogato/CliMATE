package com.jogato.climate;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Fade;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;


public class TransitionFragment extends Fragment {
    private String mCaption;
    private TextView loadingCaption;

    ImageView gifImage;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setEnterTransition(new Slide());
            setExitTransition(new Fade());
        }

        if(((MainActivity)getActivity()).getSupportActionBar() != null) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("CLIMATE");
        }

        if(getArguments() != null) {
            mCaption = getArguments().getString("caption");
        }
        else{
            mCaption = "Getting Requested Results";
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transition, container, false);
        loadingCaption = (TextView) v.findViewById(R.id.loading_caption);
        loadingCaption.setText(mCaption);
        ((MainActivity)getActivity()).setDrawerAccess(false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        gifImage = (ImageView) view.findViewById(R.id.gifImageView);
        gifImage.setImageResource(R.drawable.gifimage);
        gifImage.setBackgroundColor(Color.parseColor("#68BBEB"));
        view.setBackgroundColor(Color.parseColor("#68BBEB"));

    }

}
