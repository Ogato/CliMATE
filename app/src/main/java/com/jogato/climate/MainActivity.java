package com.jogato.climate;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String USER_QUERY_KEY = "location";
    public static ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_options, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_text, null);
        final EditText editText = view.findViewById(R.id.user_query);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Search for City Forecast")
                .setView(view)
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String user_query = editText.getText().toString().toLowerCase();
                        if(user_query.length() > 0) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            Bundle bundle = new Bundle();
                            bundle.putString(USER_QUERY_KEY, user_query);
                            Fragment fragment = new ResultFragment();
                            fragment.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Please input a location (i.e 'Mountain View')", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        alertDialog.show();

        return true;
    }

}
