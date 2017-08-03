package com.jogato.climate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String USER_CITY_KEY = "city";
    private static final String USER_STATE_KEY = "state";
    private String mSelectedState;
    private static Map<String, String> stateAbbrMap;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mOptions;
    private ActionBarDrawerToggle mDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stateAbbrMap = new HashMap<>();
        mOptions = getResources().getStringArray(R.array.navigation_options);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_option_items, mOptions));
        mDrawerList.setOnItemClickListener(new DrawerOptionClickListener());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_options, menu);
        Resources res = getResources();
        final String[] states = res.getStringArray(R.array.state_array);
        String [] abbr = res.getStringArray(R.array.state_abbr_array);
        for(int i = 1; i < states.length; i++){
            stateAbbrMap.put(states[i], abbr[i]);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_text, null);
        final EditText editText = view.findViewById(R.id.user_query);
        final Spinner stateSpinner = view.findViewById(R.id.states);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.state_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapter);
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
                            Fragment fragment = new ResultFragment();
                            fragment.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
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

    public class DrawerOptionClickListener implements ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(i == 2){
                User.getInstance().getmUserAuthState().signOut();
                Toast.makeText(MainActivity.this,"Log out successful",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        }
    }


}
