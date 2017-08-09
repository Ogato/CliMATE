package com.jogato.climate;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Fade;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class AccountFragment extends Fragment {
    private EditText mUserEmail;
    private EditText mUserName;
    private RadioButton mUserMale;
    private RadioButton mUserFemale;
    private RadioButton mUserNoPref;
    private Button mUserSave;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setEnterTransition(new Fade());
            setExitTransition(new Fade());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        mUserEmail = v.findViewById(R.id.user_email);
        mUserName = v.findViewById(R.id.user_name);
        mUserMale = v.findViewById(R.id.user_male);
        mUserFemale = v.findViewById(R.id.user_female);
        mUserNoPref = v.findViewById(R.id.user_none);


        mUserSave = v.findViewById(R.id.user_save_info);
        mUserSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUserEmail.getText().toString().isEmpty() ||
                        (!mUserMale.isChecked() && !mUserFemale.isChecked() && !mUserNoPref.isChecked())){
                    Toast.makeText(getActivity(), "Email and clothing preference are required", Toast.LENGTH_SHORT).show();

                }
                else {
                    User.getInstance().setmUserEmail(mUserEmail.getText().toString());
                    User.getInstance().setmUserName(mUserName.getText().toString());
                    if(mUserMale.isChecked()){
                        User.getInstance().setmUserPreference("male");
                    }
                    else if(mUserFemale.isChecked()){
                        User.getInstance().setmUserPreference("female");
                    }
                    else{
                        User.getInstance().setmUserPreference("none");
                    }
                    saveInfo();

                }
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserEmail.setText(User.getInstance().getmUserEmail());
        mUserName.setText(User.getInstance().getmUserName());
        if (User.getInstance().getmUserPreference() != null) {
            switch (User.getInstance().getmUserPreference()) {
                case "male":
                    mUserMale.setChecked(true);
                    break;
                case "female":
                    mUserFemale.setChecked(true);
                    break;
                case "none":
                    mUserNoPref.setChecked(true);
                    break;
                default:
                    break;
            }
        }
    }


    private void saveInfo(){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query updateChild = databaseReference.child("users").child(User.getInstance().getmUserId());
        updateChild.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Map<String, Object>userInfo = new HashMap<>();
                    userInfo.put("email", User.getInstance().getmUserEmail());
                    userInfo.put("name", User.getInstance().getmUserName());
                    userInfo.put("preference", User.getInstance().getmUserPreference());
                    userInfo.put("history", User.getInstance().getmUserHistory());
                    databaseReference.child("users").child(User.getInstance().getmUserId()).setValue(userInfo);
                    HistoryAndPreferenceSource.getInstance().updateInfo();
                }
                else{
                    databaseReference.child("users").child(User.getInstance().getmUserId()).child("email").setValue(User.getInstance().getmUserEmail());
                    databaseReference.child("users").child(User.getInstance().getmUserId()).child("name").setValue(User.getInstance().getmUserName());
                    databaseReference.child("users").child(User.getInstance().getmUserId()).child("preference").setValue(User.getInstance().getmUserPreference());
                }
                Toast.makeText(getActivity(), "Changes Saved", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new MainFragment(), "main").commit();
                getActivity().getSupportFragmentManager().beginTransaction().remove(AccountFragment.this).commit();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
}
