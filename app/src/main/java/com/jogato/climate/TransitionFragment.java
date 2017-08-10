package com.jogato.climate;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by jogato on 8/7/17.
 */

public class TransitionFragment extends Fragment {
    private String mCaption;
    private TextView loadingCaption;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setEnterTransition(new Slide());
            setExitTransition(new Fade());
        }

        mCaption = getArguments().getString("caption");
    }

    private LinearLayout mLinearLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transition, container, false);
        loadingCaption = v.findViewById(R.id.loading_caption);
        loadingCaption.setText(mCaption);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLinearLayout = view.findViewById(R.id.transition);
        ColorDrawable[] color = {new ColorDrawable(Color.RED), new ColorDrawable(Color.BLUE)};
        TransitionDrawable trans = new TransitionDrawable(color);
        mLinearLayout.setBackground(trans);
        trans.startTransition(5000);
    }

}
