package com.jogato.climate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.facebook.login.widget.LoginButton;


public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private Button mLogin;
    private Button mGoogleButton;
    private LoginButton mFacebookButton;
    private Button mSignUp;
    private EditText mEmail;
    private EditText mPassword;
    private CallbackManager mCallbackManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        mEmail = (EditText) v.findViewById(R.id.login_email);
        mPassword = (EditText) v.findViewById(R.id.login_password);
        mGoogleApiClient = MainActivity.getmGoogleApiClient();
        mAuth = FirebaseAuth.getInstance();

        mLogin = (Button) v.findViewById(R.id.login_button);
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
                                        //User.getInstance().setHistoryAndPrefs();
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

        mGoogleButton = (Button) v.findViewById(R.id.google_signin);
        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


        mFacebookButton = (LoginButton) v.findViewById(R.id.facebook_button);
        mFacebookButton.setFragment(this);
        mFacebookButton.setReadPermissions("email");
        mCallbackManager = CallbackManager.Factory.create();
        mFacebookButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("JO_INFO", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("JO_INFO", "facebook:onCancel");
                updateUI(null);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("JO_INFO", "facebook:onError", error);
                updateUI(null);
            }
        });

        mSignUp = (Button) v.findViewById(R.id.sign_up);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SignUpFragment(), "signup").commit();
            }
        });
        return v;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("JO_INFO", "HERE2");
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

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



    private void handleFacebookAccessToken(AccessToken token) {
        Log.i("JO_INFO", "HERE400");
        Log.d("JO_INFO", "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
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
                            Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
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
        String toastText = (user == null) ? "Signed out" : "Signed in as " + user.getEmail();
        Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();

        if(user == null){
            Toast.makeText(getActivity(), "Unable to login", Toast.LENGTH_SHORT).show();
        }
        else{
            User.getInstance().setmUserEmail(mAuth.getCurrentUser().getEmail());
            User.getInstance().setmUserId(mAuth.getCurrentUser().getUid());
            User.getInstance().setmUserName(mAuth.getCurrentUser().getDisplayName());
            final SharedPreferences pref = getActivity().getSharedPreferences("app_opened_count", Context.MODE_PRIVATE);
            HistoryAndPreferenceSource.getInstance().setHistoryAndPrefs(new HistoryAndPreferenceSource.HistoryAndPreferencesListener() {
                @Override
                public void onHistoryAndPreferencesResults(Boolean setPref) {
                    int count = pref.getInt("app_opened_count", 0);
                    if (count == 0 || !setPref) {
                        SharedPreferences.Editor editor = pref.edit();
                        int new_count = count + 1;
                        editor.putInt("app_opened_count", new_count);
                        editor.apply();
                        Fragment fragment = new AccountFragment();
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "account").addToBackStack("account").commit();
                    }
                    else{
                        Fragment mainFragment = new MainFragment();
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, mainFragment, "main").addToBackStack("main").commit();
                    }
                }
            });
        }
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
