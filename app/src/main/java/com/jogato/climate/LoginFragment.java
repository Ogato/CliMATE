package com.jogato.climate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private Button mLogin;
    private Button mGoogleButton;
    private Button mSignUp;
    private EditText mEmail;
    private EditText mPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("JO_INFO", "view");
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        mEmail = v.findViewById(R.id.email);
        mPassword = v.findViewById(R.id.password);

        mLogin =  v.findViewById(R.id.login);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String user_email = mEmail.getText().toString();
                final String user_password = mPassword.getText().toString();

                if(user_password.length() >= 5 && user_email.contains("@") || user_email.contains(".com") || user_email.contains(".org") || user_email.contains(".net")){
                    mAuth.signInWithEmailAndPassword(user_email, user_password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.i("JO_INFO", "signInWithEmail:onComplete:" + task.isSuccessful());
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Log.i("JO_INFO", "signInWithEmail:failed", task.getException());
                                        Toast.makeText(getActivity(), R.string.auth_failed,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(getActivity(), "Successful Login", Toast.LENGTH_SHORT).show();
                                        User.getInstance().setmUserEmail(user_email);
                                        User.getInstance().setmUserId(mAuth.getCurrentUser().getUid());
                                        User.getInstance().setHistoryAndPrefs();
                                        updateUI(mAuth.getCurrentUser());
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(getActivity(), "FAILED", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mGoogleButton = v.findViewById(R.id.google_signin);
        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        mSignUp = v.findViewById(R.id.sign_up);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("JO_INFO", "SIGNUP");
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                authenticateWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                updateUI(null);
            }
        }
    }

    private void authenticateWithGoogle(GoogleSignInAccount acct) {
        Log.d("JO_INFO", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("JO_INFO", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("JO_INFO", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        Log.i("JO_INFO", "UPDATE");
        String toastText = (user == null) ? "Signed out" : "Signed in as " + user.getEmail();
        Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();

        if(user == null){
            Log.i("JO_INFO", "NULLL");
            Toast.makeText(getActivity(), "Unable to login", Toast.LENGTH_SHORT).show();
        }
        else{
            Log.i("JO_INFO", "INNNN");
            User.getInstance().setmUserEmail(mAuth.getCurrentUser().getEmail());
            User.getInstance().setmUserId(mAuth.getCurrentUser().getUid());
            User.getInstance().setmUserName(mAuth.getCurrentUser().getDisplayName());
            User.getInstance().setHistoryAndPrefs();
            SharedPreferences pref = getActivity().getSharedPreferences("app_opened_count", Context.MODE_PRIVATE);
            int count = pref.getInt("app_opened_count", 0);
            Log.i("JO_IMPORTANT", ""+count);
            if (count == 0 || User.getInstance().getmUserPreference().equals("")) {
                SharedPreferences.Editor editor = pref.edit();
                int new_count = count + 1;
                editor.putInt("app_opened_count", new_count);
                editor.apply();
                Log.i("JO_INFO", "ACCOUNT");
                Fragment fragment = new AccountFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, "account").addToBackStack("account").commit();
            }
            else{
                Log.i("JO_INFO", "MAIN");
                Fragment mainFragment = new MainFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, mainFragment, "main").addToBackStack("main").commit();
            }
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }



}
