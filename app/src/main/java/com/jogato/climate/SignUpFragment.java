package com.jogato.climate;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by jogato on 8/9/17.
 */

public class SignUpFragment extends Fragment {

    private EditText mEmail;
    private EditText mPassword;
    private Button mSubmit;

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
        View v = inflater.inflate(R.layout.fragment_signup, container, false);
        if(((MainActivity)getActivity()).getSupportActionBar() != null){
            ((MainActivity)getActivity()).getSupportActionBar().hide();
        }
        mEmail = (EditText) v.findViewById(R.id.signup_email);
        mPassword = (EditText) v.findViewById(R.id.signup_pw);
        mSubmit = (Button) v.findViewById(R.id.submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(password.length() < 5){
                    Toast.makeText(getActivity(), "Password must be at least 5 characters", Toast.LENGTH_SHORT).show();
                }
                else if(!email.contains(".com") && !email.contains(".org") && !email.contains(".net")){
                    Toast.makeText(getActivity(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
                }
                else{
                    createAccount();
                }
            }
        });

        return v;
    }

    private void createAccount(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("JO_INFO", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(), R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getActivity(), "Account created, Please sign in",Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment(), "login").commit();
                            getActivity().getSupportFragmentManager().beginTransaction().remove(SignUpFragment.this).commit();
                        }

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(((MainActivity)getActivity()).getSupportActionBar() != null){
            ((MainActivity)getActivity()).getSupportActionBar().hide();
        }
        MainActivity.mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(((MainActivity)getActivity()).getSupportActionBar() != null){
            ((MainActivity)getActivity()).getSupportActionBar().show();
        }
        MainActivity.mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

    }
}
