package com.jogato.climate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;


public class AccountFragment extends Fragment {
    private EditText mUserEmail;
    private EditText mUserPassword;
    private EditText mUserName;
    private RadioButton mUserGender;
    private Button mUserSave;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        mUserSave = v.findViewById(R.id.user_save_info);
        mUserSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainFragment fragment = new MainFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment, null)
                        .addToBackStack(null).commit();
            }
        });
        return v;
    }
}
