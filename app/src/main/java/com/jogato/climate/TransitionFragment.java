package com.jogato.climate;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Fade;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by jogato on 8/7/17.
 */

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

        if(getArguments() != null) {
            mCaption = getArguments().getString("caption");
        }
        else{
            mCaption = "Getting Requested Results";


        }
    }

    private LinearLayout mLinearLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transition, container, false);
        loadingCaption = (TextView) v.findViewById(R.id.loading_caption);
        loadingCaption.setText(mCaption);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*mLinearLayout = (LinearLayout) view.findViewById(R.id.transition);
        ColorDrawable[] color = {new ColorDrawable(Color.RED), new ColorDrawable(Color.BLUE)};
        TransitionDrawable trans = new TransitionDrawable(color);
        mLinearLayout.setBackground(trans);
        trans.startTransition(6000);*/


        gifImage = (ImageView) view.findViewById(R.id.gifImageView);


        Random rand = new Random();
        int gifcount = rand.nextInt(4);


        if (gifcount == 0) {
            gifImage.setImageResource(R.drawable.gifimagethree);
            gifImage.setBackgroundColor(Color.parseColor("#000000"));
            view.setBackgroundColor(Color.parseColor("#000000"));

        } else if (gifcount == 1){

            gifImage.setImageResource(R.drawable.gifimagetwo);
            gifImage.setBackgroundColor(Color.parseColor("#80D3F8"));
            view.setBackgroundColor(Color.parseColor("#80D3F8"));



        } else {
            gifImage.setImageResource(R.drawable.gifimageone);
            gifImage.setBackgroundColor(Color.parseColor("#69BDEE"));
            view.setBackgroundColor(Color.parseColor("#69BDEE"));
        }



    }


}
