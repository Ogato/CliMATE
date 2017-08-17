package com.jogato.climate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String USER_CITY_KEY = "city";
    private static final String USER_STATE_KEY = "state";
    private static Map<String, String> stateAbbrMap;
    public static ListView mDrawerList;
    private String[] mOptions;
    public static  DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Fragment mMainFragment;
    private String mSelectedState;
    private static GoogleApiClient mGoogleApiClient;
    private DatePicker mDatePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        mOptions = getResources().getStringArray(R.array.navigation_options);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_option_items, mOptions));
        mDrawerList.setOnItemClickListener(new DrawerOptionClickListener());

        setupDrawer();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String city = getIntent().getStringExtra(USER_CITY_KEY);
        String state = getIntent().getStringExtra(USER_STATE_KEY);

        if(currentUser == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment(), "login").commit();
        }
        else if(city != null && !User.getInstance().getmUserPreference().isEmpty()){
            HistoryAndPreferenceSource.getInstance().deleteHandledData();
            Bundle bundle = new Bundle();
            bundle.putString(USER_CITY_KEY, city);
            bundle.putString(USER_STATE_KEY, state);

            Bundle caption = new Bundle();
            caption.putString("caption", "Getting Requested Results...");

            Fragment resultFragment = new ResultFragment();
            Fragment transition = new TransitionFragment();
            resultFragment.setArguments(bundle);
            transition.setArguments(caption);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, resultFragment, "result");
            fragmentTransaction.replace(R.id.overlay_container, transition, "transition").commit();
        }
        else {
            User.getInstance().setmUserEmail(currentUser.getEmail());
            User.getInstance().setmUserId(currentUser.getUid());
            User.getInstance().setmUserName(currentUser.getDisplayName());
            HistoryAndPreferenceSource.getInstance().setHistoryAndPrefs(new HistoryAndPreferenceSource.HistoryAndPreferencesListener() {
                @Override
                public void onHistoryAndPreferencesResults(Boolean setPref) {
                    if (!setPref) {
                        Fragment fragment = new AccountFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "account").commit();
                    } else if (setPref) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment(), "main").commit();

                    } else {
                        Toast.makeText(MainActivity.this, "Error reading from database", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_options, menu);
        Resources res = getResources();
        final String[] states = res.getStringArray(R.array.state_array);
        String [] abbr = res.getStringArray(R.array.state_abbr_array);
        stateAbbrMap = new HashMap<>();
        for(int i = 1; i < states.length; i++){
            stateAbbrMap.put(states[i], abbr[i]);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_text, null);
        final EditText editText = (EditText) view.findViewById(R.id.user_query);
        final Spinner stateSpinner = (Spinner) view.findViewById(R.id.states);

        mDatePicker = (DatePicker) view.findViewById(R.id.date_picker);
        mDatePicker.setMinDate(System.currentTimeMillis() - 1000);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        final String currentYear = calendar.get(Calendar.YEAR) + "";
        final String currentMonth = calendar.get(Calendar.MONTH) + "";
        final String currentDay = calendar.get(Calendar.DATE) + "";
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.state_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapter);

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if(item.getTitle().equals("home")){
            MainFragment main = (MainFragment) getSupportFragmentManager().findFragmentByTag("main");
            if(main ==  null){
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment(), "main").commit();
                return true;
            }
            else if(!main.isVisible()){
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment(), "main").commit();
                return true;
            }
            else{
                return true;
            }
        }

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedState = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Search for City Forecasts")
                .setView(view)
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String user_query = editText.getText().toString().toLowerCase();
                        if(user_query.length() > 0 && !mSelectedState.isEmpty() && stateAbbrMap.containsKey(mSelectedState)) {
                            Bundle bundle = new Bundle();
                            bundle.putString(USER_CITY_KEY, user_query);
                            bundle.putString(USER_STATE_KEY, stateAbbrMap.get(mSelectedState));

                            if (validRequestDate(mDatePicker.getDayOfMonth()+"", mDatePicker.getMonth()+"", mDatePicker.getYear()+"", currentDay, currentMonth, currentYear)) {
                                Fragment fragment = new ResultFragment();
                                Fragment transition = new TransitionFragment();
                                fragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().replace(R.id.overlay_container, transition, "transition").commit();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "result").commit();
                            }else{
                                Toast.makeText(MainActivity.this, "Request saved when forecast available", Toast.LENGTH_LONG).show();
                                WeatherSource.pushDatetoSchedule(mDatePicker.getDayOfMonth()+"", mDatePicker.getMonth()+"", mDatePicker.getYear()+"", user_query, stateAbbrMap.get(mSelectedState));
                            }
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Please input a city and select a state", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        alertDialog.show();

        return true;
    }


    public boolean validRequestDate(String dayDate,String monthDate,String yearDate, String currentDay, String currentMonth, String currentYear){

        if (yearDate.equals(currentYear)){
            if (monthDate.equals(currentMonth)){
                if (Integer.parseInt(dayDate)  <= Integer.parseInt(currentDay) + 3){
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public static GoogleApiClient getmGoogleApiClient(){
        return mGoogleApiClient;
    }

    public void sendResults(String city, String state){
        Bundle bundle = new Bundle();
        bundle.putString(USER_CITY_KEY, city);
        bundle.putString(USER_STATE_KEY, state);
        Bundle caption = new Bundle();
        caption.putString("caption", "Loading requested results");
        Fragment fragment = new ResultFragment();
        Fragment transition = new TransitionFragment();
        fragment.setArguments(bundle);
        transition.setArguments(caption);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.overlay_container, transition, "transition");
        ft.replace(R.id.fragment_container, fragment, "result").commit();
    }



   @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MainFragment currentFrag = (MainFragment) getSupportFragmentManager().findFragmentByTag("main");
        AccountFragment accountFragment = (AccountFragment) getSupportFragmentManager().findFragmentByTag("account");
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag("login");
        SignUpFragment signUpFragment = (SignUpFragment) getSupportFragmentManager().findFragmentByTag("signup");
        TransitionFragment transitionFragment = (TransitionFragment) getSupportFragmentManager().findFragmentByTag("transition");
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if (transitionFragment == null || !transitionFragment.isVisible()) {
                if (currentFrag != null && currentFrag.isVisible()) {
                    finish();
                    return true;
                } else if (accountFragment != null && accountFragment.isVisible()) {
                    Toast.makeText(this, "Please save account info", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (loginFragment != null && loginFragment.isVisible()) {
                    finish();
                    return true;
                } else if (signUpFragment != null && signUpFragment.isVisible()) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment(), "login").commit();
                    return true;
                } else {
                    mMainFragment = new MainFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mMainFragment, "main").commit();
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Unable to connect to database", Toast.LENGTH_SHORT).show();
    }


    private class DrawerOptionClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            MainFragment main = (MainFragment) getSupportFragmentManager().findFragmentByTag("main");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if(i == 0){
                Log.i("JO_INFO", "HISTORY");
                Fragment fragment = new HistoryFragment();
                Fragment transition = new TransitionFragment();
                Bundle caption = new Bundle();
                caption.putString("caption", "Loading History");
                transition.setArguments(caption);
                if(main != null){
                    Log.i("JO_INFO", "REMOVE");
                    ft.remove(main);
                }
                ft.replace(R.id.overlay_container, transition  , "transition");
                ft.replace(R.id.fragment_container, fragment  , "history").commit();
                mDrawer.closeDrawers();
                setDrawerAccess(false);

            }
            else if(i == 1){
                Fragment fragment = new AccountFragment();
                ft.replace(R.id.fragment_container, fragment, "account").commit();
                if(main != null){
                    ft.remove(main);
                }
                mDrawer.closeDrawers();
            }
            else if(i == 2){
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Toast.makeText(MainActivity.this,"Log out successful",Toast.LENGTH_SHORT).show();
                if(main != null){
                    ft.remove(main);
                }
                ft.replace(R.id.fragment_container, new LoginFragment(), "login").commit();
                mDrawer.closeDrawers();
                mDrawer.setEnabled(false);
            }
        }
    }


    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.drawer_open, R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawer.addDrawerListener(mDrawerToggle);
    }


    public void setDrawerAccess(boolean access){
        if(access){
            Log.i("JO_INFO", "UNLOCKED");
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        else{
            Log.i("JO_INFO", "LOCKED");
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent i = new Intent(this, NotificationService.class);
        stopService(i);
    }


    @Override
    public void onStop() {
        super.onStop();
        Intent i = new Intent(this, NotificationService.class);
        startService(i);
    }

}
