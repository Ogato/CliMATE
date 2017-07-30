package com.jogato.climate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {

    public static final String AUTH_KEY = "auth_key";
    public static final String USER_PASSWORD_KEY = "pw_key";

    public static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button mLogin;
    private Button mSignUp;
    private EditText mEmail;
    private EditText mPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("JO_INFO", "onAuthStateChanged:signed_in:" + user.getUid());
                    Intent i = new Intent(Login.this, MainActivity.class);
                    startActivity(i);
                } else {
                    // User is signed out
                    Log.d("JO_INFO", "onAuthStateChanged:signed_out");
                }
            }
        };

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);

        mLogin = (Button) findViewById(R.id.login);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String user_email = mEmail.getText().toString();
                final String user_password = mPassword.getText().toString();

                if(user_password.length() >= 8 && user_email.contains("@") || user_email.contains(".com") || user_email.contains(".org") || user_email.contains(".net")){
                    mAuth.signInWithEmailAndPassword(user_email, user_password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.i("JO_INFO", "signInWithEmail:onComplete:" + task.isSuccessful());
                                    Log.i("JO_INFO", "EMAIL:" + user_email + " PW: " + user_password);

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Log.i("JO_INFO", "signInWithEmail:failed", task.getException());
                                        Toast.makeText(Login.this, R.string.auth_failed,
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        });

        mSignUp = (Button) findViewById(R.id.sign_up);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
