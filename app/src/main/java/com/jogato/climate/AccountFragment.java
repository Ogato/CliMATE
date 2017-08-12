package com.jogato.climate;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.transition.Fade;
import android.support.v4.app.Fragment;
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
            setExitTransition(new Fade());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        //Initialize Views
        mUserEmail = (EditText) v.findViewById(R.id.user_email);
        mUserName = (EditText) v.findViewById(R.id.user_name);
        mUserMale = (RadioButton) v.findViewById(R.id.user_male);
        mUserFemale = (RadioButton) v.findViewById(R.id.user_female);
        mUserNoPref = (RadioButton) v.findViewById(R.id.user_no_preference);


        //Check if user has saved info
        mUserSave = (Button) v.findViewById(R.id.user_save_info);
        mUserSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUserEmail.getText().toString().isEmpty() ||
                        (!mUserMale.isChecked() && !mUserFemale.isChecked() && !mUserNoPref.isChecked())){

                    //Hide rest of app until email/preference are supplied
                    Toast.makeText(getActivity(), "Email and clothing preference are required", Toast.LENGTH_SHORT).show();

                }
                else {

                    //Save updated information with instance of User object model
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

                    //Send the information to firebase database
                    saveInfo();

                }
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get the user's information if any was previously saved and display it
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


    //Saves user information to firebase database
    private void saveInfo(){

        //Get general reference to firebase database
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        //Get reference to the current user and immediately get a snapshot
        DatabaseReference updateChild = databaseReference.child("users").child(User.getInstance().getmUserId());
        updateChild.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //If the information does not exist, then create new fields
                if(!dataSnapshot.exists()){
                    Map<String, Object>userInfo = new HashMap<>();
                    userInfo.put("email", User.getInstance().getmUserEmail());
                    userInfo.put("name", User.getInstance().getmUserName());
                    userInfo.put("preference", User.getInstance().getmUserPreference());
                    userInfo.put("history", User.getInstance().getmUserHistory());
                    databaseReference.child("users").child(User.getInstance().getmUserId()).setValue(userInfo);

                    //Update the local User object model with information from firebase database
                    HistoryAndPreferenceSource.getInstance().updateInfo();
                }

                //If the snapshot exsits, then use the User mobel object's informaiton
                else{
                    databaseReference.child("users").child(User.getInstance().getmUserId()).child("email").setValue(User.getInstance().getmUserEmail());
                    databaseReference.child("users").child(User.getInstance().getmUserId()).child("name").setValue(User.getInstance().getmUserName());
                    databaseReference.child("users").child(User.getInstance().getmUserId()).child("preference").setValue(User.getInstance().getmUserPreference());
                }
                Toast.makeText(getActivity(), "Changes Saved", Toast.LENGTH_SHORT).show();

                //Allow the user to enter the main fragment
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment(), "main").commit();
                getActivity().getSupportFragmentManager().beginTransaction().remove(AccountFragment.this).commit();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error saving information. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });




    }
}
